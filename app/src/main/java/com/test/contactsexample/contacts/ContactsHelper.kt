package com.test.contactsexample.contacts

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * A wrapper class for fetching contacts.
 * Note: This class does not handle duplicated contacts!!
 * Heavily commented to help students.
 */
class ContactsHelper {

    // This is the function that you need to call outside of this class.
    suspend fun getContacts(context: Context): List<Contact> {

        return withContext(Dispatchers.IO) {

            val cursor = getContactCursor(context)
            val contacts = resolveContactDataFromCursor(cursor)

            // Need to release all resources held by the cursor.
            cursor?.close()

            // The last line in the "withContext" scope is the return value
            contacts
        }
    }

    private fun getContactCursor(context: Context): Cursor? {

        // The query method returns a Cursor object that lets us access the results from the query.
        return context.contentResolver.query(

            // In this example, I use the ContactsContract.Data table, in which each row is a contact.
            // This table contains contacts that are associated with apps such as whatsapp, telegram, facebook and more
            // so there are duplicates that need to be dealt with.
            ContactsContract.Data.CONTENT_URI,

            // If we provide a projection, then only the columns we select will appear in the cursor.
            // For this example, there is no projection, so all columns will be returned in the cursor.
            null,

            // This parameter is equivalent to the part in SQL query where we filter the results.
            // In this example, I'm getting all the contacts that have either a phone number or an email.
            // in SQL it would be something like this:
            // ...WHERE HAS_PHONE_NUMBER <> 0 AND (MIMETYPE = Email OR MIMETYPE = Phone)
            ContactsContract.Data.HAS_PHONE_NUMBER + "!=0 AND (" + ContactsContract.Data.MIMETYPE + "=? OR " + ContactsContract.Data.MIMETYPE + "=?)",


            // This array holds the parameters used in the selection part.
            // The question marks in the selection string will be replaced by the objects in the array, in the order they are inserted.
            arrayOf(
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            ),

            // Sorting results from the query by the contact's ID.
            ContactsContract.Data.CONTACT_ID
        )
    }

    private fun resolveContactDataFromCursor(cursor: Cursor?): List<Contact> {

        val contacts = arrayListOf<Contact>()

        if (cursor != null && cursor.count > 0 && cursor.moveToFirst()) {

            do {

                // Note that I'm not getting the phone number.
                // It's your responsibility to makes changes in order to get the phone number as well.
                val hasPhoneNumber =
                    cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1

                if (hasPhoneNumber) {
                    val contactId =
                        cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID))

                    val contactName =
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME_PRIMARY))

                    val data1 = // I strongly advise you to read about this specific column
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.DATA1))


                    val mimeType = // The mime type describes the kind of data found in columns Data1 - Data15
                        cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Data.MIMETYPE))

                    contacts.addContact(contactId, contactName, data1, mimeType)
                }

            } while (cursor.moveToNext())

        }

        return contacts
    }
}

private fun ArrayList<Contact>.addContact(
    contactId: Int,
    contactName: String,
    data1: String?,
    mimeType: String
) {

    if (data1 != null) {
        val contact = Contact()

        when (mimeType) {
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE -> contact.email = data1
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> contact.phoneNumber = data1
        }

        contact.id = contactId
        contact.name = contactName

        add(contact)
    }
}

// Simple class to represent a contact.
class Contact {
    var name: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var id: Int = 0

    override fun toString(): String {
        return "$name, $phoneNumber, $email"
    }
}