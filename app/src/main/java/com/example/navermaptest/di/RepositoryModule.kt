package com.example.navermaptest.di

import com.example.navermaptest.repository.NaverRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { NaverRepository(get()) }
}