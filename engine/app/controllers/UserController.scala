package controllers

import javax.inject.Inject

import dal.UserRepository
import models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.mvc.Http.RequestHeader
import views.html.helper.form

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/**
  * Created by Admin on 10-06-2017.
  */
class UserController @Inject() (repo: UserRepository, val messagesApi: MessagesApi)
                               (implicit ec: ExecutionContext) extends Controller with I18nSupport {
  import models.JsonReadWrites._

  val userForm: Form[User] = Form {
        mapping("id" -> optional(number),
          "firstname" -> optional(text),
          "lastname" -> optional(text),
          "active" -> optional(boolean))(User.apply)(User.unapply)
  }

  def index() = Action { implicit request =>
    Ok(views.html.userHome())
  }

  def getUser() = Action.async { implicit request =>
    val userId = request.queryString.get("id").flatMap(_.headOption).flatMap(s => Try(s.toInt)toOption)
    repo.getUser(userId.getOrElse(throw new RuntimeException("No user Id"))).map { user =>
      Ok(Json.toJson(user))
    }
  }

  def getAllUsers = Action.async { implicit request =>
    repo.getAllUsers().map { user =>
      Ok(Json.toJson(user))
    }
  }

  def updateUserForm = Action {  implicit request =>
    Ok(views.html.userUpdate(userForm))
  }

  def updateUser = Action.async { implicit request =>
    userForm.bindFromRequest.fold(
      badForm => Future.successful(BadRequest(badForm.errorsAsJson)),
      userReq => {
        repo.updateUser(userReq).map { user =>
          Ok(Json.toJson(userReq))
        }
        //Future(Ok(views.html.userHome()))
      }
    )
  }
}
