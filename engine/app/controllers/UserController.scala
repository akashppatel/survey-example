package controllers

import javax.inject.Inject

import dal.UserRepository
import models.User
import play.api.data.{Form}
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}


import scala.concurrent.{Future, ExecutionContext}

/**
  * Created by Admin on 10-06-2017.
  */
class UserController @Inject() (repo: UserRepository, val messagesApi: MessagesApi)
                               (implicit ec: ExecutionContext) extends Controller with I18nSupport{
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

  def getUser(userId: Int) = Action.async {

    repo.getUser(userId).map { user =>
      Ok(Json.toJson(user))
    }
  }
}
