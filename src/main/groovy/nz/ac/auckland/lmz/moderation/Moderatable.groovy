package nz.ac.auckland.lmz.moderation

import groovy.transform.CompileStatic

import javax.validation.constraints.NotNull

/**
 * Defines the contract for Entities that can be moderated.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
@CompileStatic
public interface Moderatable<EntityType extends Moderatable<EntityType>> {

    /**
     * This field is for the user that submitted the entity for moderation. Should be the UPI of the user, though may
     * be the EPR university id if that is not available at the time of moderation. Consider preventing submission
     * until a UPI is available.
     */
    @NotNull
    public String getModSubmitter();

    /** @see #getModSubmitter */
    public void setModSubmitter(String modSubmitter);

    /**
     * The administrator/moderator that moderated this entity. This field should only ever be null if the entity has
     * not yet been moderated.
     */
    public String getModAdmin();

    /** @see #getModAdmin */
    public void setModAdmin(String modAdmin);

    /**
     * The comment provided by the moderator when approving or declining the moderation. Should only be required on a
     * declined moderation, but that validation is left up to the implementation.
     */
    public String getModComment();

    /** @see #getModComment */
    public void setModComment(String modComment);

    /**
     * Used to maintain a reference between a published entity, and its' draft, or a draft and its' published entity.
     * As there may be only a draft, or only a published version, this field can be null.
     */
    public EntityType getModReference();

    /** @see #getModReference */
    public void setModReference(EntityType modReference);

    /**
     * Used to determine what stage of moderation this entity is in.
     */
    @NotNull
    public String getModStatus();

    /** @see #getModStatus */
    public void setModStatus(String modStatus);

}