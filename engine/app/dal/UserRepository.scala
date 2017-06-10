package dal

import javax.inject.Inject


import models.User
import play.api.Logger
import play.api.libs.json._
import play.modules.reactivemongo.{NamedDatabase, ReactiveMongoApi}
import reactivemongo.api.ReadPreference
import play.modules.reactivemongo.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by Admin on 09-06-2017.
  */
class UserRepository  @Inject()( @NamedDatabase("surveydb") val surveyMongoApi: ReactiveMongoApi )(implicit ec: ExecutionContext) {

  private def userMappingCollection: Future[JSONCollection] = surveyMongoApi.database.map(_.collection[JSONCollection]("users"))

  val logger = Logger(this.getClass)

  import models.JsonReadWrites._

  def getUser(id: Int): Future[Option[User]] = {
    logger.debug(s" Fetcing use with userId: $id")
    userMappingCollection.flatMap {
      _.find(Json.obj("id" -> id))
        .one[User](ReadPreference.primary)
    }
  }
}
