package dal

import javax.inject.Inject

import models.User
import play.api.Logger
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.Action
import play.modules.reactivemongo.{NamedDatabase, ReactiveMongoApi}
import reactivemongo.api.{Cursor, ReadPreference}
import play.modules.reactivemongo.json._
import reactivemongo.play.json.collection.JSONCollection
import views.html.helper.form

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by Admin on 09-06-2017.
  */
class UserRepository  @Inject()( @NamedDatabase("surveydb") val surveyMongoApi: ReactiveMongoApi )(implicit ec: ExecutionContext) {

  private def userMappingCollection: Future[JSONCollection] = surveyMongoApi.database.map(_.collection[JSONCollection]("users"))

  val logger = Logger(this.getClass)

  import models.JsonReadWrites._

  def getUser(id: Int): Future[Option[User]] =  {

    logger.debug(s" Fetcing use with userId: $id")
    userMappingCollection.flatMap {
      _.find(Json.obj("id" -> id))
        .one[User](ReadPreference.primary)
    }
  }

  def getAllUsers(): Future[List[User]] = {
    userMappingCollection.flatMap{
      _.find(Json.obj()).cursor[User]().collect[List](100,  Cursor.FailOnError[List[User]]())
    }
  }

  def updateUser(user: User): Future[Option[User]] = for {
    transactions <- userMappingCollection
    writeResult <- transactions.update(
      Json.obj("id"->user.id.getOrElse{ throw new RuntimeException("User details does not exist.")}),
        Json.obj("$set" -> Json.obj("id" -> user.id.getOrElse{ throw new RuntimeException("Incorrect user Id")},
          "firstname" -> user.firstname,
          "lastname" -> user.lastname,
          "active" -> true)))
  } yield {
    if (writeResult.ok)
      Some(user)
    else {
      logger.debug(s"DB update error: ${writeResult.errmsg}")
      None
    }
  }

}
