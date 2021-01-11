package com.skyblueos.actors.database

import com.skyblueos.actors.models.{Chat, User}
import org.bson.codecs.configuration.{CodecProvider, CodecRegistries, CodecRegistry}
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros

protected object DatabaseConfig {

  val mongoClient: MongoClient = MongoClient()
  val databaseName: String = "Users"

  //for users
  val codecProvider: CodecProvider = Macros.createCodecProvider[User]()
  val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(codecProvider),
    DEFAULT_CODEC_REGISTRY
  )

  val database: MongoDatabase = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
  val collectionName: String = "Users"
  val collection: MongoCollection[User] = database.getCollection(collectionName)

  //for chat
  val codecProviderForChat: CodecProvider = Macros.createCodecProvider[Chat]()
  val codecRegistryForChat: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(codecProviderForChat),
    DEFAULT_CODEC_REGISTRY
  )

  val databaseForChat: MongoDatabase = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistryForChat)
  val collectionNameForChat: String = "Chats"
  val collectionForChat: MongoCollection[Chat] = databaseForChat.getCollection(collectionNameForChat)
}
