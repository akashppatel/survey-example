package models

import play.api.libs.json.Json

/**
  * Created by Admin on 09-06-2017.
  */
object JsonReadWrites {
  implicit val formatUser = Json.format[User]
}
