package com.sharkaboi.yogapartner.modules.asana_list.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sharkaboi.yogapartner.NavGraphDirections
import com.sharkaboi.yogapartner.R
import com.sharkaboi.yogapartner.common.extensions.observe
import com.sharkaboi.yogapartner.common.extensions.showToast
import com.sharkaboi.yogapartner.data.models.Asana
import com.sharkaboi.yogapartner.data.models.AsanaDifficulty
import com.sharkaboi.yogapartner.databinding.FragmentAsanaListBinding
import com.sharkaboi.yogapartner.modules.RecyclerViewAdapter
import com.sharkaboi.yogapartner.modules.asana_list.adapter.AsanaListAdapter
import com.sharkaboi.yogapartner.modules.asana_list.vm.AsanaListViewModel
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class AsanaListFragment : Fragment() {
    private lateinit var adapter: AsanaListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var list: List<Asana>
    private var _binding: FragmentAsanaListBinding? = null
    private val binding get() = _binding!!
    private val navController get() = findNavController()
    private val viewModel by viewModels<AsanaListViewModel>()


    class AsanaListFragment : Fragment() {
        private lateinit var adapter: AsanaListAdapter
        private lateinit var recyclerView: RecyclerView
        private lateinit var recyclerViewAdapter: RecyclerViewAdapter
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
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentAsanaListBinding.inflate(inflater, container, false)
            val view = binding.root

            // RecyclerView 초기화
            recyclerView = binding.rvAsanas
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // 어댑터 초기화

            recyclerView.adapter = adapter

            // 아이템 리스트 생성
            val asanaList = listOf(
                Asana("1", "1", "1", AsanaDifficulty.DIFFICULT, "1", true),
                Asana("1", "1", "1", AsanaDifficulty.DIFFICULT, "1", true),
                Asana("1", "1", "1", AsanaDifficulty.DIFFICULT, "1", true)
            )

            // 아이템 리스트를 어댑터에 전달
            adapter.submitList(asanaList)

            return view
        }

        override fun onDestroyView() {
            binding.rvAsanas.adapter = null
            _binding = null
            super.onDestroyView()
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            setListeners()
            setupRecyclerView()
            setupObservers()
        }

        private fun openSettings() {
            val destinations = NavGraphDirections.openSettings()
            navController.navigate(destinations)
        }

        private fun setupObservers() {
            observe(viewModel.currentList) {
                adapter.submitList(it)
                binding.tvEmptyHint.isVisible = it.isEmpty()
            }
            observe(viewModel.isLoading) {
                binding.progress.isVisible = it
            }
            observe(viewModel.errors) {
                showToast(it)
            }
        }

        private fun setupRecyclerView() {
            val rvAsanas = binding.rvAsanas
            this.adapter = AsanaListAdapter(
                onClick = {
                    openYogaAsana(it)
                }
            )
            rvAsanas.adapter = this.adapter
            rvAsanas.layoutManager = LinearLayoutManager(context)
            rvAsanas.itemAnimator = DefaultItemAnimator()
            rvAsanas.setHasFixedSize(true)
        }

        private fun openYogaAsana(asana: Asana) {
            val action = NavGraphDirections.openAsanaDetails(asana)
            navController.navigate(action)
        }

        private fun setListeners() {
            binding.fabOpenPose.setOnClickListener {
                checkPermission()
            }

            binding.fabOpenClass.setOnClickListener {
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
            this.permLauncher.launch(android.Manifest.permission.CAMERA)
        }

        private fun handlePermissionCheck(wasGranted: Boolean) {
            if (!wasGranted) {
                showToast("Need Camera permission")
                return
            }

            val direction = NavGraphDirections.openAsanaPose()
            navController.navigate(direction)
        }

        private inline fun <T> observe(liveData: LiveData<T>, crossinline action: (T) -> Unit) {
            liveData.observe(viewLifecycleOwner, { action(it) })
        }

        // AsanaListAdapter 클래스 정의
        inner class AsanaListAdapter(private val onClick: (Asana) -> Unit) :
            RecyclerView.Adapter<AsanaListAdapter.ViewHolder>() {

            private var dataList: List<Asana> = emptyList()

            inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val textView: TextView = itemView.findViewById(R.id.textView)
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_layout, parent, false)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val item = dataList[position]
                holder.textView.text = item.name
                holder.itemView.setOnClickListener { onClick(item) }
            }

            override fun getItemCount(): Int {
                return dataList.size
            }

            fun submitList(list: List<Asana>) {
                dataList = list
                notifyDataSetChanged()
            }
        }
    }
}
