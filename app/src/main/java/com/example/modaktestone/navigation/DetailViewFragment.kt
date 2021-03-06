package com.example.modaktestone.navigation

import ZoomOutPageTransformer
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.modaktestone.R
import com.example.modaktestone.databinding.FragmentDetailBinding
import com.example.modaktestone.databinding.ItemBestcontentBinding
import com.example.modaktestone.databinding.ItemPagerBinding
import com.example.modaktestone.databinding.ItemRepeatboardBinding
import com.example.modaktestone.navigation.model.ContentDTO
import com.example.modaktestone.navigation.model.UserDTO
import com.example.modaktestone.navigation.viewPager.ImageSlideFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_image_slide.view.*
import kotlinx.android.synthetic.main.item_repeatboard.*
import org.koin.android.ext.android.bind
import java.text.SimpleDateFormat

class DetailViewFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var currentUserUid: String? = null
    var region: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        //?????????
        firestore = FirebaseFirestore.getInstance()
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        firestore?.collection("users")?.document(currentUserUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                region = regionDTO?.region.toString()
            }

        //???????????? ????????? ???????????? ?????????
        binding.detailviewRecyclerviewRepeatboard.adapter = RepeatboardRecyclerViewAdapter()
        binding.detailviewRecyclerviewRepeatboard.layoutManager = LinearLayoutManager(this.context)

        //?????????????????? ???????????? ?????????
        binding.detailviewRecyclerviewBestcontent.adapter = BestContentRecyclerViewAdapter()
        binding.detailviewRecyclerviewBestcontent.layoutManager = LinearLayoutManager(this.context)

        //????????? ????????? ?????????
        binding.detailviewRecyclerviewHotcontent.adapter = HotContentRecyclerViewAdapter()
        binding.detailviewRecyclerviewHotcontent.layoutManager = LinearLayoutManager(this.context)

        //?????? ??? ?????? ???????????? ?????????
        binding.detailviewRecyclerviewSociety.adapter = DetailSocietyRecyclerViewAdapter()
        binding.detailviewRecyclerviewSociety.layoutManager = LinearLayoutManager(this.context)

        //????????? ?????? ???????????? ?????????
        binding.detailviewRecyclerviewClub.adapter = DetailClubRecyclerViewAdapter()
        binding.detailviewRecyclerviewClub.layoutManager = LinearLayoutManager(this.context)

        binding.detailviewBtnRepeatboard.setOnClickListener {
            var fragment = BoardFragment()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_content, fragment)?.commit()
        }

        //?????? ?????? ?????? ?????????
        var requestItem: ArrayList<Int> = arrayListOf(
            R.drawable.information_first,
            R.drawable.information_second,
            R.drawable.information_third
        )
        var titleItem: ArrayList<String> = arrayListOf(
            "????????? ????????? ??? ????????? ?????????",
            "???????????? ?????? ?????????",
            "?????? <??? ????????????> ????????? ?????????"
        )
        var contentItem: ArrayList<String> = arrayListOf(
            "????????? ???????????? ?????? 10% ?????? ??????,\n????????? 1?????? ?????? ???????????????!",
            "????????? ????????? ??????????????????\n????????? ?????? ??? ??? ???????????????!",
            "<??? ????????????> ????????? ?????? ??? ???????????? ????????????!\n????????? ?????? ???????????? ???????????????!"
        )

        //???????????? ???????????? ?????????
        binding.viewPager.adapter = ViewPagerAdapter(requestItem, titleItem, contentItem)
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        //??????????????? ???????????? ??????????????? ??????
        binding.dotsIndicator.setViewPager2(binding.viewPager)

        //??? ?????? ??????
        getMyRegion()

        //????????? ?????? ????????????
        var eventItem: ArrayList<Int> =
            arrayListOf(
                R.drawable.event_first,
                R.drawable.event_second,
                R.drawable.event_third,
                R.drawable.event_forth
            )
        var eventTitle: ArrayList<String> =
            arrayListOf(
                "?????? <????????? ??????> ?????? ?????? ?????????",
                "?????? <????????????, ??? ????????? ??????!> ?????? ?????????",
                "<??????, ?????? ????????? _ ????????? ??????> #????????? ?????????",
                "????????? <????????? ?????????> ?????? ?????? ?????????"
            )
        var eventContent: ArrayList<String> = arrayListOf(
            "20??? ??????(1??? 2???, ??? 40???)",
            "50???(1??? 2???, ??? 100???)",
            "50???(1??? 2???, ??? 100???)",
            "30???(1??? 2???, ??? 60???)"
        )

        //????????? ???????????? ???????????? ??????????????? ?????????
        binding.viewPagerSecond.adapter =
            ViewPagerSecondAdapter(eventItem, eventTitle, eventContent)
        binding.viewPagerSecond.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotsIndicatorSecond.setViewPager2(binding.viewPagerSecond)


        //???????????? ?????? ????????????
        var welfareItem: ArrayList<Int> = arrayListOf(
            R.drawable.welfare_first,
            R.drawable.welfare_second,
            R.drawable.welfare_third
        )
        var welfareTitle: ArrayList<String> =
            arrayListOf(
                "???????????????????????? - ?????????????????? ??????",
                "???????????????????????? - ????????????",
                "??????????????? ??????????????? ???????????? ???????????? ?????????????????? ?????????"
            )

        binding.viewPagerThird.adapter = ViewPagerThirdAdapter(welfareItem, welfareTitle)
        binding.viewPagerThird.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotsIndicatorThird.setViewPager2(binding.viewPagerThird)


        //?????? 4??? ?????? ??????
        binding.detailviewBtnHomepage.setOnClickListener { v ->
            var intent = Intent(v.context, HomeRegionActivity::class.java)
            startActivity(intent)
        }

        binding.detailviewBtnNotice.setOnClickListener { v ->
            var intent = Intent(v.context, BoardContentActivity::class.java)
            intent.putExtra("destinationCategory", "????????? ????????????")
            startActivity(intent)
        }



        return view
    }

    fun getMyRegion() {
        firestore?.collection("users")?.document(currentUserUid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                binding.boardcontentTextviewMyregion.text = regionDTO?.region.toString()
            }
    }


    inner class RepeatboardRecyclerViewAdapter :
        RecyclerView.Adapter<RepeatboardRecyclerViewAdapter.CustomViewHolder>() {

        var boardDTO: List<String> =
            listOf("???????????????", "???????????????", "???????????????", "???????????????", "??????????????????", "??????????????????", "???????????????")

        inner class CustomViewHolder(val binding: ItemRepeatboardBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RepeatboardRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemRepeatboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: RepeatboardRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.itemRepeatboardTvBoardname.text = boardDTO[position]

            when (boardDTO[position]) {
                "???????????????" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("users")?.document(currentUserUid!!)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            if (documentSnapshot == null) return@addSnapshotListener
                            var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                            firestore?.collection("contents")
                                ?.whereEqualTo("region", regionDTO!!.region)
                                ?.whereEqualTo("contentCategory", "???????????????")
                                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                    contentDTOs.clear()
                                    if (documentSnapshot == null) return@addSnapshotListener
                                    for (snapshot in documentSnapshot.documents) {
                                        var item = snapshot.toObject(ContentDTO::class.java)
                                        contentDTOs.add(item!!)
                                    }
                                    if (contentDTOs.size != 0) {
                                        holder.binding.itemRepeatboardTvBoardcontent.text =
                                            contentDTOs[0].explain.toString()

                                    } else {
                                        println(contentDTOs.size)
                                    }

                                }

                        }

                }
                "???????????????" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("users")?.document(currentUserUid!!)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            if (documentSnapshot == null) return@addSnapshotListener
                            var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                            firestore?.collection("contents")
                                ?.whereEqualTo("region", regionDTO!!.region)
                                ?.whereEqualTo("contentCategory", "???????????????")
                                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                    contentDTOs.clear()
                                    if (documentSnapshot == null) return@addSnapshotListener
                                    for (snapshot in documentSnapshot.documents) {
                                        var item = snapshot.toObject(ContentDTO::class.java)
                                        contentDTOs.add(item!!)
                                    }
                                    if (contentDTOs.size != 0) {
                                        holder.binding.itemRepeatboardTvBoardcontent.text =
                                            contentDTOs[0].explain.toString()

                                    } else {
                                        println(contentDTOs.size)
                                    }

                                }

                        }
                }
                "???????????????" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("users")?.document(currentUserUid!!)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            if (documentSnapshot == null) return@addSnapshotListener
                            var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                            firestore?.collection("contents")
                                ?.whereEqualTo("region", regionDTO!!.region)
                                ?.whereEqualTo("contentCategory", "???????????????")
                                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                    contentDTOs.clear()
                                    if (documentSnapshot == null) return@addSnapshotListener
                                    for (snapshot in documentSnapshot.documents) {
                                        var item = snapshot.toObject(ContentDTO::class.java)
                                        contentDTOs.add(item!!)
                                    }
                                    if (contentDTOs.size != 0) {
                                        holder.binding.itemRepeatboardTvBoardcontent.text =
                                            contentDTOs[0].explain.toString()

                                    } else {
                                        println(contentDTOs.size)
                                    }

                                }

                        }

                }
                "???????????????" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("users")?.document(currentUserUid!!)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            if (documentSnapshot == null) return@addSnapshotListener
                            var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                            firestore?.collection("contents")
                                ?.whereEqualTo("region", regionDTO!!.region)
                                ?.whereEqualTo("contentCategory", "???????????????")
                                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                    contentDTOs.clear()
                                    if (documentSnapshot == null) return@addSnapshotListener
                                    for (snapshot in documentSnapshot.documents) {
                                        var item = snapshot.toObject(ContentDTO::class.java)
                                        contentDTOs.add(item!!)
                                    }
                                    if (contentDTOs.size != 0) {
                                        holder.binding.itemRepeatboardTvBoardcontent.text =
                                            contentDTOs[0].explain.toString()

                                    } else {
                                        println(contentDTOs.size)
                                    }

                                }

                        }
                }
                "??????????????????" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("users")?.document(currentUserUid!!)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            if (documentSnapshot == null) return@addSnapshotListener
                            var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                            firestore?.collection("contents")
                                ?.whereEqualTo("region", regionDTO!!.region)
                                ?.whereEqualTo("contentCategory", "??????????????????")
                                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                    contentDTOs.clear()
                                    if (documentSnapshot == null) return@addSnapshotListener
                                    for (snapshot in documentSnapshot.documents) {
                                        var item = snapshot.toObject(ContentDTO::class.java)
                                        contentDTOs.add(item!!)
                                    }
                                    if (contentDTOs.size != 0) {
                                        holder.binding.itemRepeatboardTvBoardcontent.text =
                                            contentDTOs[0].explain.toString()

                                    } else {
                                        println(contentDTOs.size)
                                    }

                                }

                        }
                }
                "??????????????????" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("users")?.document(currentUserUid!!)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            if (documentSnapshot == null) return@addSnapshotListener
                            var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                            firestore?.collection("contents")
                                ?.whereEqualTo("region", regionDTO!!.region)
                                ?.whereEqualTo("contentCategory", "??????????????????")
                                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                    contentDTOs.clear()
                                    if (documentSnapshot == null) return@addSnapshotListener
                                    for (snapshot in documentSnapshot.documents) {
                                        var item = snapshot.toObject(ContentDTO::class.java)
                                        contentDTOs.add(item!!)
                                    }
                                    if (contentDTOs.size != 0) {
                                        holder.binding.itemRepeatboardTvBoardcontent.text =
                                            contentDTOs[0].explain.toString()

                                    } else {
                                        println(contentDTOs.size)
                                    }
                                }
                        }
                }
                "???????????????" -> {
                    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

                    firestore?.collection("users")?.document(currentUserUid!!)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            if (documentSnapshot == null) return@addSnapshotListener
                            var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                            firestore?.collection("contents")
                                ?.whereEqualTo("region", regionDTO!!.region)
                                ?.whereEqualTo("contentCategory", "???????????????")
                                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                                    contentDTOs.clear()
                                    if (documentSnapshot == null) return@addSnapshotListener
                                    for (snapshot in documentSnapshot.documents) {
                                        var item = snapshot.toObject(ContentDTO::class.java)
                                        contentDTOs.add(item!!)
                                    }
                                    if (contentDTOs.size != 0) {
                                        holder.binding.itemRepeatboardTvBoardcontent.text =
                                            contentDTOs[0].explain.toString()

                                    } else {
                                        println(contentDTOs.size)
                                    }

                                }

                        }
                }
            }

            holder.binding.itemRepeatboardTvBoardname.setOnClickListener { v ->
                var intent = Intent(v.context, BoardContentActivity::class.java)
                intent.putExtra("destinationCategory", boardDTO[position])
                startActivity(intent)
            }

        }

        override fun getItemCount(): Int {
            return boardDTO.size
        }
    }

    inner class HotContentRecyclerViewAdapter :
        RecyclerView.Adapter<HotContentRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        var contentUidList: ArrayList<String> = arrayListOf()

        init {

            firestore?.collection("contents")?.orderBy("timestamp", Query.Direction.DESCENDING)
                ?.limit(10)?.orderBy("commentCount", Query.Direction.DESCENDING)?.limit(2)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    if (documentSnapshot == null) return@addSnapshotListener
                    for (snapshot in documentSnapshot.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }


        }

        inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): HotContentRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: HotContentRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {

            holder.binding.itemBestcontentTvTitle.text = contentDTOs[position].title

            holder.binding.itemBestcontentTvExplain.text = contentDTOs[position].explain

            holder.binding.itemBestcontentTvUsername.text = contentDTOs[position].userName

            holder.binding.itemBestcontentTvCommentcount.text =
                contentDTOs[position].commentCount.toString()

            holder.binding.itemBestcontentTvFavoritecount.text =
                contentDTOs[position].favoriteCount.toString()

            holder.binding.contentLinearLayout.setOnClickListener { v ->
                var intent = Intent(v.context, DetailContentActivity::class.java)
                if (contentDTOs[position].anonymity.containsKey(contentDTOs[position].uid)) {
                    intent.putExtra("destinationUsername", "??????")
                } else {
                    intent.putExtra("destinationUsername", contentDTOs[position].userName)
                }
                intent.putExtra("destinationTitle", contentDTOs[position].title)
                intent.putExtra("destinationExplain", contentDTOs[position].explain)
                intent.putExtra(
                    "destinationTimestamp",
                    SimpleDateFormat("MM/dd HH:mm").format(contentDTOs[position].timestamp)
                )
                intent.putExtra(
                    "destinationCommentCount",
                    contentDTOs[position].commentCount.toString()
                )
                intent.putExtra(
                    "destinationFavoriteCount",
                    contentDTOs[position].favoriteCount.toString()
                )
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                intent.putExtra("destinationUid", contentDTOs[position].uid)
                startActivity(intent)
            }

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }


    inner class BestContentRecyclerViewAdapter :
        RecyclerView.Adapter<BestContentRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {

            firestore?.collection("contents")?.orderBy("favoriteCount", Query.Direction.DESCENDING)
                ?.limit(2)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    if (documentSnapshot == null) return@addSnapshotListener
                    for (snapshot in documentSnapshot.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                    }
                    notifyDataSetChanged()
                }


        }

        inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BestContentRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: BestContentRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.itemBestcontentTvTitle.text = contentDTOs[position].title

            holder.binding.itemBestcontentTvExplain.text = contentDTOs[position].explain

            holder.binding.itemBestcontentTvUsername.text = contentDTOs[position].userName

            holder.binding.itemBestcontentTvCommentcount.text =
                contentDTOs[position].commentCount.toString()

            holder.binding.itemBestcontentTvFavoritecount.text =
                contentDTOs[position].favoriteCount.toString()
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

    inner class DetailSocietyRecyclerViewAdapter :
        RecyclerView.Adapter<DetailSocietyRecyclerViewAdapter.CustomViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("users")?.document(currentUserUid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                    firestore?.collection("contents")?.whereEqualTo("region", regionDTO!!.region)
                        ?.whereEqualTo("contentCategory", "?????? ??? ??????")
                        ?.orderBy("timestamp")?.limit(3)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            contentDTOs.clear()
                            if (documentSnapshot == null) return@addSnapshotListener
                            for (snapshot in documentSnapshot.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                            }
                            notifyDataSetChanged()
                        }
                }


        }

        inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DetailSocietyRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: DetailSocietyRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.itemBestcontentTvTitle.text = contentDTOs[position].title

            holder.binding.itemBestcontentTvExplain.text = contentDTOs[position].explain

            holder.binding.itemBestcontentTvUsername.text = contentDTOs[position].userName

            holder.binding.itemBestcontentTvCommentcount.text =
                contentDTOs[position].commentCount.toString()

            holder.binding.itemBestcontentTvFavoritecount.text =
                contentDTOs[position].favoriteCount.toString()
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

    inner class DetailClubRecyclerViewAdapter :
        RecyclerView.Adapter<DetailClubRecyclerViewAdapter.CustomViewHolder>() {

        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

        init {
            firestore?.collection("users")?.document(currentUserUid!!)
                ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (documentSnapshot == null) return@addSnapshotListener
                    var regionDTO = documentSnapshot.toObject(UserDTO::class.java)
                    firestore?.collection("contents")?.whereEqualTo("region", regionDTO!!.region)
                        ?.whereEqualTo("contentCategory", "????????? ??????")
                        ?.orderBy("timestamp")?.limit(3)
                        ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                            contentDTOs.clear()
                            if (documentSnapshot == null) return@addSnapshotListener
                            for (snapshot in documentSnapshot.documents) {
                                var item = snapshot.toObject(ContentDTO::class.java)
                                contentDTOs.add(item!!)
                            }
                            notifyDataSetChanged()
                        }
                }
        }

        inner class CustomViewHolder(val binding: ItemBestcontentBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DetailClubRecyclerViewAdapter.CustomViewHolder {
            val binding =
                ItemBestcontentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: DetailClubRecyclerViewAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.itemBestcontentTvTitle.text = contentDTOs[position].title

            holder.binding.itemBestcontentTvExplain.text = contentDTOs[position].explain

            holder.binding.itemBestcontentTvUsername.text = contentDTOs[position].userName

            holder.binding.itemBestcontentTvCommentcount.text =
                contentDTOs[position].commentCount.toString()

            holder.binding.itemBestcontentTvFavoritecount.text =
                contentDTOs[position].favoriteCount.toString()
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

    }

    inner class ViewPagerAdapter(
        itemList: ArrayList<Int>,
        titleList: ArrayList<String>,
        contentList: ArrayList<String>
    ) :
        RecyclerView.Adapter<ViewPagerAdapter.CustomViewHolder>() {
        var itemDTO = itemList

        var titleDTO = titleList

        var contentDTO = contentList

        inner class CustomViewHolder(val binding: ItemPagerBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewPagerAdapter.CustomViewHolder {
            val binding =
                ItemPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewPagerAdapter.CustomViewHolder, position: Int) {
            holder.binding.imgPager.setImageResource(itemDTO[position])

            holder.binding.pagerTvTitle.text = titleDTO[position]

            holder.binding.pagerTvContent.text = contentDTO[position]


        }

        override fun getItemCount(): Int {
            return itemDTO.size
        }


    }

    inner class ViewPagerSecondAdapter(
        eventItemList: ArrayList<Int>,
        eventTitleList: ArrayList<String>,
        eventContentList: ArrayList<String>
    ) : RecyclerView.Adapter<ViewPagerSecondAdapter.CustomViewHolder>() {
        var itemDTO = eventItemList
        var titleDTO = eventTitleList
        var contentDTO = eventContentList

        inner class CustomViewHolder(val binding: ItemPagerBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewPagerSecondAdapter.CustomViewHolder {
            val binding =
                ItemPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: ViewPagerSecondAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.imgPager.setImageResource(itemDTO[position])

            holder.binding.pagerTvTitle.text = titleDTO[position]

            holder.binding.pagerTvContent.text = contentDTO[position]
        }

        override fun getItemCount(): Int {
            return itemDTO.size
        }

    }

    inner class ViewPagerThirdAdapter(
        welfareItemList: ArrayList<Int>,
        welfareTitleList: ArrayList<String>
    ) : RecyclerView.Adapter<ViewPagerThirdAdapter.CustomViewHolder>() {
        var itemDTO = welfareItemList
        var titleDTO = welfareTitleList

        inner class CustomViewHolder(val binding: ItemPagerBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewPagerThirdAdapter.CustomViewHolder {
            val binding =
                ItemPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: ViewPagerThirdAdapter.CustomViewHolder,
            position: Int
        ) {
            holder.binding.imgPager.setImageResource(itemDTO[position])

            holder.binding.pagerTvTitle.text = titleDTO[position]

            holder.binding.pagerTvContent.visibility = View.GONE
        }

        override fun getItemCount(): Int {
            return itemDTO.size
        }

    }
}