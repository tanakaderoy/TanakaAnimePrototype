package com.tanaka.mazivanhanga.tanakaanimeprototype.room

import com.tanaka.mazivanhanga.tanakaanimeprototype.models.LatestShow
import com.tanaka.mazivanhanga.tanakaanimeprototype.util.EntityMapper


/**
 * Created by Tanaka Mazivanhanga on 07/18/2020
 */
class LatestShowEpisodeCacheMapper
constructor() : EntityMapper<LatestShowEpisodeEntity, LatestShow> {
    override fun mapFromEntity(entity: LatestShowEpisodeEntity): LatestShow {
        return LatestShow(
            entity.title,
            entity.image,
            entity.url,
            entity.currentEpURL,
            entity.currentEp
        )
    }

    override fun mapToEntity(domainModel: LatestShow): LatestShowEpisodeEntity {
        return LatestShowEpisodeEntity(
            title = domainModel.title,
            image = domainModel.image,
            url = domainModel.url,
            currentEp = domainModel.currentEp,
            currentEpURL = domainModel.currentEpURL
        )
    }

    fun mapFromEntitiesList(entities: List<LatestShowEpisodeEntity>): List<LatestShow> {
        return entities.map { mapFromEntity(it) }
    }
}