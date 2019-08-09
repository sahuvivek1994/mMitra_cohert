package tech.inscripts.ins_armman.mMitra.homeactivity

import android.content.Context
import android.database.Cursor
import org.json.JSONException
import org.json.JSONObject
import tech.inscripts.ins_armman.mMitra.R
import tech.inscripts.ins_armman.mMitra.data.database.DatabaseContract
import tech.inscripts.ins_armman.mMitra.data.model.SyncRegistrationDetails
import tech.inscripts.ins_armman.mMitra.data.model.syncing.BeneficiaryDetails
import tech.inscripts.ins_armman.mMitra.data.model.syncing.FormDetails
import tech.inscripts.ins_armman.mMitra.data.model.syncing.QuestionAnswer
import tech.inscripts.ins_armman.mMitra.utility.Constants.*
import tech.inscripts.ins_armman.mMitra.utility.Utility
import java.util.ArrayList

class MainActivityPresentor : IMainActivityPresentor<IMainActivity>,IMainActivityInteractor.OnDataSync,IMainActivityInteractor.OnFormSync {




    private val FETCH_USER_DATA = 101
    private val FETCH_REGISTRATION_DATA = 102
    private val FETCH_FORMS_DATA = 103
    private val FETCH_UNSENT_FORM_COUNT = 104


    private var mIMainActivityView: IMainActivity? = null
     var mInteractor: MainActivityInteractor? = null
    private var mContext: Context? = null
    private var mUsername: String = ""
    private var mPassword:String = ""
    private var mUserId:String = ""
    //private var mImei: String =""
    private var mImei: ArrayList<String>?=null

    var utility= Utility()
    private val mOnQueryFinished = object : IMainActivityPresentor.OnQueryFinished {
        override fun onSuccess(cursor: Cursor, id: Int) {
            when (id) {
                FETCH_USER_DATA -> if (cursor.moveToFirst()) {
                    mUsername = cursor.getString(cursor.getColumnIndex(DatabaseContract.LoginTable.COLUMN_USERNAME))
                    mPassword = cursor.getString(cursor.getColumnIndex(DatabaseContract.LoginTable.COLUMN_PASSWORD))
                    mUserId=cursor.getString(cursor.getColumnIndex(DatabaseContract.LoginTable.COLUMN_USER_ID))
                    mImei =utility.getDeviceImeiNumber(mContext!!)
                   /* mUserId="1"
                    mUsername="test_user1"
                    mPassword="test_user1"
                    mImei="869432026925037"*/
                    //val arogyasakhiName = cursor.getString(cursor.getColumnIndex(DatabaseContract.LoginTable.COLUMN_NAME))
                  //  mIHomeActivityView?.setArogyasakhiName(arogyasakhiName)
                }

                FETCH_UNSENT_FORM_COUNT -> if (cursor.moveToFirst())
                    mIMainActivityView?.setUnsentFormsCount(cursor.getInt(0))

                FETCH_REGISTRATION_DATA -> if (cursor.count > 0) {
                    onFetchedRegistrationData(cursor)
                } else
                    syncUnsentForms()
            }
        }

        override fun onSuccess() {

        }

        override fun onFailure() {

        }
    }


    override fun fetchRegistrationData() {
    mIMainActivityView?.showProgressBar(mContext?.getString(R.string.uploading_forms)!!)

        ResetFailureTask(object : IMainActivityPresentor.OnResetTaskCompleted {
            override fun onResetCompleted() {
                mInteractor?.fetchRegistrationDetails(FETCH_REGISTRATION_DATA)
            }
        }, mInteractor)
            .execute()
    }

    override fun onFetchedRegistrationData(cursor: Cursor) {
        var regDetails = SyncRegistrationDetails()
        /*regDetails.setusername(mUsername)
        regDetails.setpassword(mPassword)
        */
        regDetails.setusername(mUsername)
        regDetails.setpassword(mPassword)
       regDetails.setImei(mImei)

        val regData = ArrayList<BeneficiaryDetails>()
        while (cursor.moveToNext()) {
            val details = BeneficiaryDetails()

            val uniqueId = cursor.getString(cursor.getColumnIndex(DatabaseContract.RegistrationTable.COLUMN_UNIQUE_ID))
            details.setUniqueId(uniqueId)
            details.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.RegistrationTable.COLUMN_NAME)))
            details.setAddress(cursor.getString(cursor.getColumnIndex(DatabaseContract.RegistrationTable.COLUMN_ADDRESS)))
            details.setMobNo(cursor.getString(cursor.getColumnIndex(DatabaseContract.RegistrationTable.COLUMN_MOBILE_NO)))
            details.setLmp(cursor.getString(cursor.getColumnIndex(DatabaseContract.RegistrationTable.COLUMN_LMP_DATE)))
            details.setDob(cursor.getString(cursor.getColumnIndex(DatabaseContract.RegistrationTable.COLUMN_AGE)))
            details.setEducation(cursor.getString(cursor.getColumnIndex(DatabaseContract.RegistrationTable.COLUMN_EDUCATION)))
            details.setCreatedOn(cursor.getString(cursor.getColumnIndex(DatabaseContract.RegistrationTable.COLUMN_CREATED_ON)))
            regData.add(details)
        }
        if (regData.size == 0)
            return

        regDetails.setRegData(regData)

        mInteractor?.sendRegistrationBasicDetails(regDetails, this)
    }

    override fun fetchUnsentFormsCount() {
    mInteractor?.fetchUnsentFormsCount(FETCH_UNSENT_FORM_COUNT)
    }

    override fun syncUnsentForms() {
        var cursor = mInteractor?.checkUnsentForms()
        if (cursor!!.moveToFirst()) {

            var details = FormDetails()
            var answerList = ArrayList<QuestionAnswer>()

            details.setusername(mUsername)
            details.setpassword(mPassword)
            details.setImei(mImei)
            var uniqueId = cursor?.getString(cursor.getColumnIndex(DatabaseContract.FilledFormStatusTable.COLUMN_UNIQUE_ID))
            details.setUniqueId(uniqueId)
            var formId = cursor.getString(cursor.getColumnIndex(DatabaseContract.FilledFormStatusTable.COLUMN_FORM_ID))
            details.setFormId(formId)

            var cursorForm = mInteractor?.fetchFormData(cursor.getString(cursor.getColumnIndex(DatabaseContract.FilledFormStatusTable.COLUMN_ID)))
            while (cursorForm?.moveToNext()!!) {
                var answer = QuestionAnswer()
                answer.setKeyword(cursorForm.getString(cursorForm.getColumnIndex(DatabaseContract.QuestionAnswerTable.COLUMN_QUESTION_KEYWORD)))
                answer.setAnswer(cursorForm.getString(cursorForm.getColumnIndex(DatabaseContract.QuestionAnswerTable.COLUMN_ANSWER_KEYWORD)))
                answer.setCreatedOn(cursorForm.getString(cursorForm.getColumnIndex(DatabaseContract.QuestionAnswerTable.COLUMN_CREATED_ON)))
                answerList.add(answer)
            }

            details.setData(answerList)

            mInteractor?.sendForms(details, this)
        }
        else
        {
            mIMainActivityView?.hideProgressBar()
            fetchUnsentFormsCount()
        }
    }
    private fun markImproperVisitToSync(uniqueId: String, formId: String, errorMsg: String) {
        mInteractor?.updateFormFailureStatus(uniqueId, formId, errorMsg)
        syncUnsentForms()
    }

    private fun markImproperRegistrationToSync(uniqueId: String, errorMsg: String) {
        mInteractor?.updateRegistrationFailureStatus(uniqueId, errorMsg)
        mInteractor?.fetchRegistrationDetails(FETCH_REGISTRATION_DATA)
    }
    override fun attachView(view: IMainActivity) {
        mIMainActivityView = view
        mContext = mIMainActivityView?.getContext()
        mInteractor = MainActivityInteractor(mContext, mOnQueryFinished)
        mInteractor?.fetchLoginDetails(FETCH_USER_DATA)
    }


    override fun detachView() {
    }

    override fun onSuccessFullySyncRegData(jsonObjectResponse: JSONObject) {
        if (!jsonObjectResponse.optBoolean(STATUS) && (jsonObjectResponse.optString(RESPONSE) == AUTHENTICATION_FAILED ||
                    jsonObjectResponse.optString(RESPONSE) == INVALID_IMEI)
        )
            onFailure(mContext?.getString(R.string.authentication_error_msg)!!)
        else {
            try {
                val jsonArrayResponse = jsonObjectResponse.getJSONArray(DATA)
                mInteractor?.updateRegistrationSyncStatus(jsonArrayResponse)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            mInteractor?.fetchRegistrationDetails(FETCH_REGISTRATION_DATA)
        }
    }

    override fun onSuccessfullySyncForm(jsonObject: JSONObject) {
        if (jsonObject.optBoolean(STATUS)) {
            mInteractor?.updateFormSyncStatus(jsonObject.optString(UNIQUE_ID), jsonObject.optString(FORM_ID))
            syncUnsentForms()
        } else {
            if (jsonObject.optString(RESPONSE) == AUTHENTICATION_FAILED || jsonObject.optString(RESPONSE) == INVALID_IMEI)
                onFailure(mContext?.getString(R.string.authentication_error_msg)!!)
            else {
                mInteractor?.updateFormFailureStatus(
                    jsonObject.optString(UNIQUE_ID),
                    jsonObject.optString(FORM_ID),
                    jsonObject.optString(RESPONSE, INVALID_DATA)
                )
                syncUnsentForms()
            }

        }
    }

    override fun onFailure(message: String) {
        mIMainActivityView?.hideProgressBar()
        mIMainActivityView?.showSnackBar(message)
        fetchUnsentFormsCount()
    }

    override fun getLoginDetail(userDetails : ArrayList<String>) {
        var cur : Cursor= mInteractor?.getLoginDetails()!!
        if (cur != null && cur.moveToFirst())
            do {
                var fullName = cur.getString(cur.getColumnIndex("name"))
                var username = cur.getString(cur.getColumnIndex("username"))
                userDetails.add(fullName)
                userDetails.add(username)
            } while (cur.moveToNext())
    }


}