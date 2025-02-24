/*
 * Copyright (c) 2025, Vitaliy Levonyuk
 * Licensed under the MIT License.
 */
package sample.currency.converter.inject

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import sample.currency.converter.ConverterViewModel
import sample.currency.data.CurrencyRepository

val appModule = module {
    single { CurrencyRepository() }
    viewModel { ConverterViewModel(repository = get()) }
}
