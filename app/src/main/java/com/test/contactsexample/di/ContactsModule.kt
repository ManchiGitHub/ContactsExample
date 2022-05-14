package com.test.contactsexample.di

import com.test.contactsexample.contacts.ContactsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ContactsModule {

    @Provides
    @Singleton
    fun provideContactsGetter(): ContactsHelper {
        return ContactsHelper()
    }
}