package com.sharkaboi.yogapartner.modules.asana_list.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sharkaboi.yogapartner.NavGraphDirections
import com.sharkaboi.yogapartner.R
import com.sharkaboi.yogapartner.common.extensions.showToast
import com.sharkaboi.yogapartner.data.models.Asana
import com.sharkaboi.yogapartner.databinding.FragmentAsanaListBinding
import com.sharkaboi.yogapartner.modules.asana_list.DataItem
import com.sharkaboi.yogapartner.modules.asana_list.FirstFragment
import com.sharkaboi.yogapartner.modules.asana_list.adapter.OnItemClickListener
import com.sharkaboi.yogapartner.modules.asana_list.adapter.RecyclerViewAdapter
import com.sharkaboi.yogapartner.modules.asana_list.vm.AsanaListViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AsanaListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var dataList: DataItem

    private lateinit var list: List<Asana>
    private var _binding: FragmentAsanaListBinding? = null
    private val binding get() = _binding!!
    private val navController get() = findNavController()
    private val viewModel by viewModels<AsanaListViewModel>()
    private val permLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            handlePermissionCheck(it)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAsanaListBinding.inflate(inflater, container, false)
        val view = binding.root
        val dataList = listOf(
            DataItem("Meditaion", R.drawable.yoga1),
            DataItem("Worrior", R.drawable.worrior),
            DataItem("Tree Pose", R.drawable.yoga2),
            DataItem(" Standing Bow Pulling Pose", R.drawable.yoga3)
        )


        // 리사이클러뷰 초기화
        val recyclerView = binding.recyclerview
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = gridLayoutManager





        // 어댑터 초기화
        recyclerViewAdapter = RecyclerViewAdapter(dataList)
        recyclerView.adapter = recyclerViewAdapter

        return view
    }



    override fun onDestroyView() {
        binding.recyclerview.adapter = null
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()

    }

    private fun openSettings() {
        val destinations = NavGraphDirections.openSettings()
        navController.navigate(destinations)
    }





    private fun openYogaAsana(asana: Asana) {
        val action = NavGraphDirections.openAsanaDetails(asana)
        navController.navigate(action)
    }

    private fun setListeners() {
        binding.fabOpenPose.setOnClickListener {
            checkPermission()
        }

        binding.fabOpenClass.setOnClickListener{
            val direction = NavGraphDirections.openclass2()
            navController.navigate(direction)

        }
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrBlank()) {
                viewModel.resetSearch()
                return@doOnTextChanged
            }

            viewModel.search(text.toString())
        }
        binding.btnSettings.setOnClickListener { openSettings() }
    }

    private fun checkPermission() {
        permLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun handlePermissionCheck(wasGranted: Boolean) {
        if (!wasGranted) {
            showToast("Need Camera permission")
            return
        }

        val direction = NavGraphDirections.openAsanaPose()
        navController.navigate(direction)
    }


}