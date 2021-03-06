package tech.inscripts.ins_armman.mMitra.completeformsdetails

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.json.JSONException
import org.json.JSONObject
import tech.inscripts.ins_armman.mMitra.R
import tech.inscripts.ins_armman.mMitra.data.model.CompleteFormQnA
import tech.inscripts.ins_armman.mMitra.utility.Utility
import java.util.ArrayList

class DetailsAdapter() : RecyclerView.Adapter<DetailsAdapter.ViewHolder>() {

    lateinit var mContext : Context
    var question : String?=null
    var answer : String?=null
    var language : String?=null
    var formId: Int = 0
    var utility= Utility()
    private var mDetails: ArrayList<CompleteFormQnA>?=null

    constructor(mContext: Context, formId: Int, mDetails: ArrayList<CompleteFormQnA>?) : this() {
        this.mContext = mContext
        this.formId = formId
        this.mDetails = mDetails
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): DetailsAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.activity_list_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDetails?.size!!
    }

    override fun onBindViewHolder(holder: DetailsAdapter.ViewHolder, position: Int) {
    var c : CompleteFormQnA = mDetails?.get(position)!!
        answer = c.answer
        var ansArray: List<String> = answer?.split(",")!!.map { it.trim() }
        question=c.question
     // var ans : String =""
       // var finalAns : ArrayList<String> ?=null
        if (answer!!.contains("{") && question!!.contains("{")) {
            try {
                var obj = JSONObject(c.question)
                var obj1 = JSONObject(c.answer)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                Log.d("ADAPTER_ANS :", c.answer)
                language = utility.getLanguagePreferance(mContext)
                if (language!!.isEmpty()) {
                    utility.setApplicationLocale(mContext, "en")
                } else {
                    utility.setApplicationLocale(mContext, language!!)
                }
                question = obj.getString(this.language)
                answer = obj1.getString(this.language)
               /* holder.txtQuestion.text = question
                if(ansArray.size>1) {
                    var i = 0
                    while (i < ansArray.size) {
                        if (ansArray[i].contains("{")) {
                            var a= ansArray.get(i)
                            mDetails?.set(position,c)?.answer =a
                            var obj1 = JSONObject()
                           *//* language = utility.getLanguagePreferance(mContext)
                            if (language!!.isEmpty()) {
                                utility.setApplicationLocale(mContext, "en")
                            } else {
                                utility.setApplicationLocale(mContext, language!!)
                            }*//*
                            answer = obj1.getString(this.language)
                            finalAns?.add(answer!!)
                            i++
                        }
                    }
                    ans = finalAns?.joinToString()!!
                }
                else{
                    var obj1 = JSONObject(c.answer)
                    ans = obj1.getString(this.language)
                }
                holder.txtAnswer.text = ans*/
                holder.txtQuestion.text = question
                holder.txtAnswer.text = answer
                println("settext question :$question")
                println("settext answer :$answer")

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else if (question!!.contains("{") && !answer!!.contains("{")) {
            try {
                val objQue = JSONObject(c.question)
                language = utility.getLanguagePreferance(mContext)
                if (language!!.isEmpty()) {
                    utility.setApplicationLocale(mContext, "en")
                } else {
                    utility.setApplicationLocale(mContext, language!!)
                }
                question = objQue.getString(this.language)
                holder.txtQuestion.text = question
                holder.txtAnswer.text = answer

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else if (!question!!.contains("{") && answer!!.contains("{")) {
            try {
                val objAns = JSONObject(c.answer)
                language = utility.getLanguagePreferance(mContext)
                if (language!!.isEmpty()) {
                    utility.setApplicationLocale(mContext, "en")
                } else {
                    utility.setApplicationLocale(mContext, language!!)
                }
                answer = objAns.getString(this.language)
                holder.txtQuestion.text = question
                holder.txtAnswer.text = answer
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else {
            holder.txtQuestion.text = question
            holder.txtAnswer.text = answer
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var txtQuestion: TextView
        internal var txtAnswer: TextView

        init {
            txtQuestion = itemView.findViewById(R.id.tvQuestion)
            txtAnswer = itemView.findViewById(R.id.tvAnswer)
        }
    }
}