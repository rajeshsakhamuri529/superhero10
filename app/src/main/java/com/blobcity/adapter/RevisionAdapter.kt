package com.blobcity.adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.FileProvider
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.database.DatabaseHandler
import com.blobcity.interfaces.RevisionItemClickListener
import com.blobcity.model.RevisionModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.revision_list_item.view.*
import kotlinx.android.synthetic.main.revision_list_item.view.rid
import kotlinx.android.synthetic.main.revision_list_item.view.rootLayout
import kotlinx.android.synthetic.main.revision_list_item.view.shortdescription
import kotlinx.android.synthetic.main.revision_list_item.view.tags
import kotlinx.android.synthetic.main.revision_list_item.view.thumbnail
import kotlinx.android.synthetic.main.revision_list_item.view.timetotaken
import kotlinx.android.synthetic.main.revision_list_item.view.title
import kotlinx.android.synthetic.main.revision_list_new.view.*
import java.io.File

class RevisionAdapter(
    val context: Context,
    val revisionItemList: List<RevisionModel>,
    val pdfItemClickListener: RevisionItemClickListener
) : RecyclerView.Adapter<RevisionAdapter.RevisionViewHolder>() {
    var databaseHandler: DatabaseHandler?= null
    lateinit var holder1: RevisionViewHolder
    var position1:Int = -1
    var isMoreOpen:Boolean = false
    lateinit var mFile: File
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RevisionViewHolder {
       // Log.e("revision adapter","onCreateViewHolder.....revisionItemList...."+revisionItemList.size)
        databaseHandler = DatabaseHandler(context);
        return RevisionViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.revision_list_new, p0, false)
        )
    }

    override fun getItemCount(): Int {
       // Log.e("revision adapter","getItemCount.....revisionItemList...."+revisionItemList)
       // return 2
       return revisionItemList.size
    }

    override fun onBindViewHolder(holder: RevisionViewHolder, position: Int) {
        //Log.e("revision adapter","onBindViewHolder...single..revisionItemList...."+revisionItemList[position])

        //Log.e("revision adapter","onBindViewHolder....revisionItemList...."+revisionItemList[position].imageLink)
        holder1 = holder
        holder.rid.text = revisionItemList[position].sno
        holder.title.text = revisionItemList[position].tilte
        holder.timetotaken.text = revisionItemList[position].timeToRead
        holder.shortdiscription.text = revisionItemList[position].shortDescription

        var str: String? = revisionItemList[position].tags?.replace("[","")
        var str1: String? = str?.replace("]","")

        //Log.e("revision fragment",".....str1..."+str1);



        val strarray = str1?.split(",")?.toTypedArray()
       // Log.e("revision fragment",".....strarray..."+strarray?.size);
       // Log.e("revision fragment",".....strarray..."+ (strarray?.get(0)));
        val builder = StringBuilder()
        for (name in strarray!!) {
            println(name)

            builder.append("#")
                .append(name.trim())
                .append(" ")
        }
      //  Log.e("revision fragment",".....builder..."+builder);
        holder.tags.text = builder

        holder.topLayout.setOnClickListener {
            revisionItemList[position]?.let { it1 -> pdfItemClickListener.onClick(it1) }
        }

        Glide.with(context)
            .load(revisionItemList[position].imageLink)
            .into(holder.thumbnail)

        var status = databaseHandler!!.getBookReadStatus(revisionItemList[position].rId);
        Log.e("pdf viewer","status....."+status);
        if(status.equals("1")){
           // databaseHandler!!.updateBookaReadStatus(rid,"1");
            //markread.text ="Mark as Read"
            holder.tick.visibility = View.VISIBLE
            holder.tick.background = context.resources.getDrawable(R.drawable.ic_tick_green_circle1)
        }else{
            holder.tick.visibility = View.GONE
        }


        if(isMoreOpen){
            Log.e("revision adapter","position1....."+position1)
            Log.e("revision adapter","position....."+position)
            if(position1 == position){
                isMoreOpen = false
                Log.e("revision adapter","is more open.........")
                holder.topLayout.isEnabled = true
                holder.menuRL.visibility = View.GONE

            }
        }

        holder.moreRL.setOnClickListener {
            holder.topLayout.isEnabled = false
            holder.menuRL.visibility = View.VISIBLE

            pdfItemClickListener.onMoreButttonClicked("more",position)
            Log.e("revision frag","more.......position..."+position);
            var status = databaseHandler!!.getBookReadStatus(revisionItemList[position].rId);
            Log.e("pdf viewer","status....."+status);
            if(status.equals("0")){
                holder.markread.text ="Mark as Read"
                holder.tick1.background = context.resources.getDrawable(R.drawable.book_read_status)
            }else{
                holder.markread.text ="Mark as Unread"
                holder.tick1.background = context.resources.getDrawable(R.drawable.ic_tick_green_circle1)
            }

        }

        holder.markreadLL.setOnClickListener {
            holder.topLayout.isEnabled = true
            holder.menuRL.visibility = View.GONE

            Log.e("revision frag","more.......");
            //pdfItemClickListener.onMoreButttonClicked("marked")
            var status = databaseHandler!!.getBookReadStatus(revisionItemList[position].rId);
            Log.e("pdf viewer","status....."+status);
            if(status.equals("0")){
                databaseHandler!!.updateBookaReadStatus(revisionItemList[position].rId,"1");
                holder.markread.text ="Mark as Read"
                holder.tick1.background = context.resources.getDrawable(R.drawable.book_read_status)
            }else{
                databaseHandler!!.updateBookaReadStatus(revisionItemList[position].rId,"0");
                holder.markread.text ="Mark as Unread"
                holder.tick1.background = context.resources.getDrawable(R.drawable.ic_tick_green_circle1)
            }
            notifyDataSetChanged()
        }

        holder.shareLL.setOnClickListener {
            holder.topLayout.isEnabled = true
            holder.menuRL.visibility = View.GONE

            //pdfItemClickListener.onMoreButttonClicked("share")
            Log.e("revision frag","more.......");
            mFile = File(context.getExternalFilesDir(null), revisionItemList[position].filename+".pdf")
            sendFile(context)
        }

    }

    public fun closeMore(position:Int){
        Log.e("revision adapter","close more.....")
        /*holder1.topLayout.isEnabled = true
        holder1.menuRL.visibility = View.GONE
        holder1.frameRL.visibility = View.GONE*/
        position1 = position
        isMoreOpen = true
        notifyDataSetChanged()
    }

    fun sendFile(context:Context) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("application/pdf")
        // val dirpath = context.getFilesDir() + File.separator + "directory"
        // val file = File(dirpath + File.separator + "file.txt")
        val uri = FileProvider.getUriForFile(context, "com.yomplex.simple.files", mFile)
        //grant permision for app with package "packegeName", eg. before starting other app via intent
        context.grantUriPermission(
            "com.yomplex.simple",
            uri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // Workaround for Android bug.
        // grantUriPermission also needed for KITKAT,
        // see https://code.google.com/p/android/issues/detail?id=76683
        /*if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
        {
            val resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList)
            {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(packageName, attachmentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }*/
        if (intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(intent)
        }
    }

    class RevisionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail = itemView.thumbnail
        var rid = itemView.rid
        val title = itemView.title
        val timetotaken = itemView.timetotaken
        val shortdiscription = itemView.shortdescription
        val tags = itemView.tags
        val topLayout = itemView.topRL

        val moreRL = itemView.moreRL
        val tick = itemView.tick


        val menuRL = itemView.menuRL
        val markreadLL = itemView.markreadLL
        val tick1 = itemView.tick1
        val markread = itemView.markread
        val shareLL = itemView.shareLL

        //val frameRL = itemView.frameRL
        //val frameRL1 = itemView.frameRL1
    /*val singleTopic = itemView.rl_single_topics
    val tv_topic_number = itemView.tv_topic_number
    val tv_topic_name = itemView.tv_topic_name
    val iv_progress1 = itemView.iv_progress_icon1
    val iv_progress2 = itemView.iv_progress_icon2
    val iv_progress3 = itemView.iv_progress_icon3

    val topLayout = itemView.top_layout
    val lockLayout = itemView.lock_layout

    val lock = itemView.lock
    val lock_txt = itemView.lock_txt*/
    }
}