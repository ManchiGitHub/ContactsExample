//package com.test.contactsexample.utils
//
//
//private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)
//
//@ActivityRetainedScoped
//class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {
//
//    private object PreferenceKeys {
//        val selectedMealType = stringPreferencesKey("mealType")
//        val selectedMealTypeId = intPreferencesKey("mealTypeId")
//        val selectedDietType = stringPreferencesKey("dietType")
//        val selectedDietTypeId = intPreferencesKey("dietTypeId")
//    }
//
//    private val dataStore: DataStore<Preferences> = context.dataStore
//
//    suspend fun saveMealAndDietType(MealType: String, MealTypeId: Int, DietType: String, DietTypeId: Int) {
//        dataStore.edit { preferences ->
//            preferences[PreferenceKeys.selectedMealType] = MealType
//            preferences[PreferenceKeys.selectedMealTypeId] = MealTypeId
//            preferences[PreferenceKeys.selectedDietType] = DietType
//            preferences[PreferenceKeys.selectedDietTypeId] = DietTypeId
//        }
//    }
//
//    val readMealAndDietType: Flow<MealAndDietType> = dataStore.data.catch { exception ->
//        if (exception is IOException) {
//            emit(emptyPreferences())
//        } else {
//            throw exception
//        }
//    }
//
//    .map { preferences ->
//        val selectedMealType = preferences[PreferenceKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
//        val selectedMealTypeId = preferences[PreferenceKeys.selectedMealTypeId] ?: 0
//        val selectedDietType = preferences[PreferenceKeys.selectedDietType] ?: DEFAULT_DIET_TYPE
//        val selectedDietTypeId = preferences[PreferenceKeys.selectedDietTypeId] ?: 0
//        MealAndDietType(
//            selectedMealType, selectedMealTypeId, selectedDietType, selectedDietTypeId
//        )
//    }
//}
//