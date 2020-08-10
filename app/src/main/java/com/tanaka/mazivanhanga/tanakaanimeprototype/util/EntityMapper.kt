package com.tanaka.mazivanhanga.tanakaanimeprototype.util


/**
 * Created by Tanaka Mazivanhanga on 07/18/2020
 */
interface EntityMapper<Entity, DomainModel> {

    fun mapFromEntity(entity: Entity): DomainModel

    fun mapToEntity(domainModel: DomainModel): Entity

}