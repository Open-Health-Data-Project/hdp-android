package org.openhdp.hdt.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.openhdp.hdt.data.DataTrackingDatabase
import org.openhdp.hdt.other.Constants.DATABASE_NAME
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDataTrackingDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        DataTrackingDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideCategoryDAO(db: DataTrackingDatabase) = db.getCategoryDAO()

    @Singleton
    @Provides
    fun provideCategoryWithStopwatchesDAO(db: DataTrackingDatabase) = db.getCategoryWithStopwatchesDAO()

    @Singleton
    @Provides
    fun provideDailyChangedDurationDAO(db: DataTrackingDatabase) = db.getDailyChangedDurationDAO()

    @Singleton
    @Provides
    fun provideIndependentCategoriesDAO(db: DataTrackingDatabase) = db.getIndependentCategoriesDAO()

    @Singleton
    @Provides
    fun provideStopwatchDAO(db: DataTrackingDatabase) = db.getStopwatchDAO()

    @Singleton
    @Provides
    fun provideStopwatchWithDailyChangedDurationDAO(db: DataTrackingDatabase) = db.getStopwatchWithDailyChangedDurationDAO()

    @Singleton
    @Provides
    fun provideStopwatchWithTimestampsDAO(db: DataTrackingDatabase) = db.getStopwatchWithTimestampsDAO()

    @Singleton
    @Provides
    fun provideTimestampDAO(db: DataTrackingDatabase) = db.getTimestampDAO()
}