package models

case class UserInfo(id: Int,
                    firstname: String,
                    lastname: String,
                    email: String,
                    username: String,
                    created: Long)


object UserInfo {

}