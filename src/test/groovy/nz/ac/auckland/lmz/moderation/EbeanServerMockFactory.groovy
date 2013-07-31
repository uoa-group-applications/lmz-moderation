package nz.ac.auckland.lmz.moderation

import com.avaje.ebean.EbeanServer
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean;

/**
 * Created mocked ebean instances.
 * <p>Author: <a href="http://gplus.to/tzrlk">Peter Cummuskey</a></p>
 */
public class EbeanServerMockFactory implements InitializingBean, FactoryBean<EbeanServer> {

    EbeanServer ebeanServer;

    @Override
    void afterPropertiesSet() throws Exception {
        this.ebeanServer = [:] as EbeanServer;
    }

    @Override
    EbeanServer getObject() throws Exception {
        return ebeanServer;
    }

    @Override
    Class<?> getObjectType() {
        return EbeanServer.class;
    }

    @Override
    boolean isSingleton() {
        return true;
    }

}
