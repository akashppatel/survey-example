package controllers

import javax.inject.Inject

import models.User
import play.api.libs.json.Json
import play.api.mvc._
import utilities.JwtUtility

import scala.concurrent.Future

/**
  * Created by Admin on 26-07-2017.
  */
case class UserInfo(id: Int,
                    firstName: String,
                    lastName: String,
                    email: String)

//case class User(email: String, userId: String)

case class UserRequest[A](userInfo: UserInfo, request: Request[A]) extends WrappedRequest(request)

class SecuredAuthenticator @Inject()(/*dataSource: DataSource*/) extends Controller {
  //implicit val formatUserDetails = Json.format[User]
  import models.JsonReadWrites._

  object JWTAuthentication extends ActionBuilder[UserRequest] {
    def invokeBlock[A](request: Request[A], block: (UserRequest[A]) => Future[Result]): Future[Result] = {
      val jwtToken = request.headers.get("jw_token").getOrElse("")

      if (JwtUtility.isValidToken(jwtToken)) {
        JwtUtility.decodePayload(jwtToken).fold {
          Future.successful(Unauthorized("Invalid credential"))
         } { payload =>
          println("payload:"+payload)
          val userCredentials = Json.parse(payload).validate[User].get

          // Replace this block with data source
          val maybeUserInfo = getUser(userCredentials.firstname.get, userCredentials.lastname.get, userCredentials.id.get)//dataSource.getUser(userCredentials.email, userCredentials.userId)

          maybeUserInfo.fold(Future.successful(Unauthorized("Invalid credential")))(userInfo => block(UserRequest(userInfo, request)))
        }
      } else {
        Future.successful(Unauthorized("Invalid credential"))
      }
    }

    def getUser(firstName: String, lastName: String, userId: Int): Option[UserInfo] =
      if (firstName == "John" && userId == 1) {
        Some(UserInfo(userId, firstName, lastName,"emai@id"))
      } else {
        None
      }
  }

}