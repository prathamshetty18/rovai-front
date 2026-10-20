package com.roavai.parent.core.di

import com.roavai.parent.data.fake.*
import com.roavai.parent.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FakeAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChildRepository(impl: FakeChildRepository): ChildRepository

    @Binds
    @Singleton
    abstract fun bindRobotRepository(impl: FakeRobotRepository): RobotRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: FakeHomeRepository): HomeRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(impl: FakeActivityRepository): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(impl: FakeProgressRepository): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindGoalsRepository(impl: FakeGoalsRepository): GoalsRepository

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(impl: FakeNotificationsRepository): NotificationsRepository
}
