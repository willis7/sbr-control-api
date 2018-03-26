import com.google.inject.AbstractModule
import java.time.Clock

import play.api.{Configuration, Environment}
import repository.hbase.HBaseRestLocalUnitRepository
import repository.{HBaseRestLocalUnitRepository, LocalUnitRepository}
import services.{DataAccess, HBaseRestDataAccess}

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module(environment: Environment, configuration: Configuration) extends AbstractModule {
  override def configure() = {
    bind(classOf[DataAccess]).to(classOf[HBaseRestDataAccess])
    bind(classOf[LocalUnitRepository]).to(classOf[HBaseRestLocalUnitRepository])
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
  }
}
