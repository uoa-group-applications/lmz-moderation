package nz.ac.auckland.lmz.moderation

import com.avaje.ebean.EbeanServer
import com.avaje.ebean.ExpressionList
import nz.ac.auckland.common.stereotypes.UniversityComponent
import nz.ac.auckland.lmz.ClassUtils
import nz.ac.auckland.lmz.errors.PersistException
import nz.ac.auckland.lmz.persist.Identifiable
import nz.ac.auckland.lmz.service.Saviour

import javax.inject.Inject
import java.lang.annotation.Annotation
import java.lang.reflect.Field

/**
 * Provides moderation operations with common functionality.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@UniversityComponent
public class ModerationUtils {

	public static final String MSG_BYPASS = 'The moderation process has been bypassed for this entity';

	@Inject Saviour saviour;

    public void publish(Moderatable source, Moderatable target) {
        List<String> publishableProperties = findPublishableProperties(target.class)*.name;
        ClassUtils.safeCopy(target, source.properties, publishableProperties + [
                'modAdmin', // Required for approval tracking.
                'modSubmitter', // Required to know where to send emails.
		        'enabled' // Required to enable/disable entities
        ]);
    }

    public List<Field> findPublishableProperties(Class target) {
        if (!target || target.class == Object) {
            // Object won't have any publishable annotations on it.
            return [];
        }

        // scan the provided target's class for publishable fields.
        List<Field> results = target.declaredFields.findAll { Field field ->
            return field.declaredAnnotations.any(this.&testPublishable);
        }

        // recursively collect all the superclass data as well.
        return results + findPublishableProperties(target.superclass);
    }

    protected boolean testPublishable(Annotation annotation) {
        return annotation instanceof Publishable;
    }

	/**
	 * Finds the pending draft for the provided entity, if one or more exist. If more than one exist, the newest one
	 * will be returned.
	 * @param published The published entity to find the latest draft for.
	 * @return The latest draft for the published entity, or null if not found.
	 * @throws PersistException
	 */
	public Moderatable findExistingDraft(Moderatable published, boolean onlyPending = true) throws PersistException {
		return saviour.query { EbeanServer ebean ->

			ExpressionList expression = ebean
					.find(published.class)
					.where()
					.eq('modReference', published);

			if (onlyPending) {
				expression.eq('modStatus', ModerationStatus.PENDING.name())
			}

			return expression
					.orderBy('id desc')
					.setMaxRows(1)
					.findUnique();
		}
	}

	public void verifyDraftStatus(Moderatable target) throws ModerationException {
		Moderatable existingDraft = findExistingDraft(target, false);

		if (existingDraft) {

			// If the existing draft is still pending, don't let another one be created.
			if (ModerationStatus.PENDING.is(existingDraft)) {
				throw new ModerationException('moderate.verify.existing', [
						targetId: retrieveId(target),
						targetType: target.class.simpleName,
						targetDraftId: retrieveId(existingDraft)
				]);
			}

			// Otherwise, delete the old draft.
			saviour.delete(existingDraft, true);

		}
	}

	protected Serializable retrieveId(Moderatable moderatable) {
		return ClassUtils.safeCast(moderatable, Identifiable)?.id;
	}

}
