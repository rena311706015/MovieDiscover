package com.example.moviediscover

import android.app.Application
import androidx.room.Room
import com.example.moviediscover.database.AppDatabase
import com.example.moviediscover.database.MovieRepository
import com.example.moviediscover.database.MovieRepositoryImpl
import com.example.moviediscover.viewmodel.BookmarkViewModel
import com.example.moviediscover.viewmodel.DetailViewModel
import com.example.moviediscover.viewmodel.HomeViewModel
import com.example.moviediscover.viewmodel.MainViewModel
import com.example.moviediscover.viewmodel.MovieListViewModel
import com.example.moviediscover.viewmodel.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val appModule = module {
            single { Room.databaseBuilder(get(), AppDatabase::class.java, "movie").build() }
            single { get<AppDatabase>().movieDao() }
            single<MovieRepository> { MovieRepositoryImpl(get()) }
            factory<MovieRepository> { MovieRepositoryImpl(get()) }
            viewModel { MainViewModel() }
            viewModel { HomeViewModel(get()) }
            viewModel { MovieListViewModel(get()) }
            viewModel { SearchViewModel(get()) }
            viewModel { DetailViewModel(get()) }
            viewModel { BookmarkViewModel(get()) }
        }

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}