package ui.activities

import adapters.AssignedRoutesAdapter
import android.app.SearchManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.choptransit.R
import app.com.choptransit.databinding.ActivitySearchRouteBinding
import app.com.choptransit.models.response.AssignRouteData
import app.com.choptransit.utilities.Commons
import app.com.choptransit.viewmodels.AdminViewModel

class SearchRouteActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchRouteBinding
    lateinit var viewModel: AdminViewModel
    lateinit var adapter: AssignedRoutesAdapter

    var arrayList = ArrayList<AssignRouteData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_route)
        viewModel = ViewModelProvider(this)[AdminViewModel::class.java]
        setSupportActionBar(binding.toolbar)

        setObservers()
        viewModel.getAssignedRoutes()
    }

    private fun setObservers() {
        viewModel.dialogLiveData.observe(this) {
            if (it) {
                Commons.showProgress(this@SearchRouteActivity)
            } else {
                Commons.hideProgress()
            }
        }

        viewModel.assignRouteReponseLiveData.observe(this) {
            if (it.code.equals("00")) {
                arrayList = it.data
                initRoutesRecyclerview(arrayList)
            }
        }
    }

    private fun initRoutesRecyclerview(routeArrayList: ArrayList<AssignRouteData>) {
        adapter = AssignedRoutesAdapter(routeArrayList, this@SearchRouteActivity)
        binding.rvRoutes.setHasFixedSize(true)
        binding.rvRoutes.layoutManager = LinearLayoutManager(this)
        binding.rvRoutes.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView =
            searchItem?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText.toString())
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

}