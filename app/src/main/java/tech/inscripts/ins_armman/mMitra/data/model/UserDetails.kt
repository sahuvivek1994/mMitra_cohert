package tech.inscripts.ins_armman.mMitra.data.model

import com.google.gson.annotations.SerializedName

class UserDetails {
    @SerializedName("username")
    var userName: String? = null
    @SerializedName("password")
    var password: String? = null
    @SerializedName("showdata")
    private var showdata: String? = null

    fun getShowdata()
    {
        userName
    }

    fun getpassword()
    {
        password
    }

    fun getusername()
    {
        userName
    }


    fun setShowdata(showdata: String) {
        this.showdata = showdata
    }

    fun setpassword(pass: String)
    {
        this.password = pass
    }

    fun setusername(user: String)
    {
        this.userName = user
    }



    override fun toString(): String {
        return "UserDetails{" +
                "userName='" + userName + '\''.toString() +
                ", password='" + password + '\''.toString() +
                ", showdata='" + showdata + '\''.toString() +
                '}'.toString()
    }

}
