package com.example.repository

import com.example.models.Todo
import com.example.models.User
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class TodoRepository: Repository {

    override suspend fun addUser(email: String, displayName: String, passwordHash: String): User? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Users.insert { user ->
                user[Users.email] = email
                user[Users.displayName] = displayName
                user[Users.passwordHash] = passwordHash
            }
        }
        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun findUser(userId: Int) = dbQuery {
        Users.select { Users.userId.eq(userId) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String) = dbQuery {
        Users.select { Users.email.eq(email) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun addTodo(userId: Int, todo: String, done: Boolean): Todo? {
        var statement : InsertStatement<Number>? = null
        dbQuery {
            statement = Todos.insert {
                it[Todos.userId] = userId
                it[Todos.todo] = todo
                it[Todos.done] = done
            }
        }
        return rowToTodo(statement?.resultedValues?.get(0))
    }

    override suspend fun getTodos(userId: Int): List<Todo> {
        return dbQuery {
            Todos.select {
                Todos.userId.eq((userId))
            }.mapNotNull { rowToTodo(it) }
        }
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) return null
        return User(
            userId = row[Users.userId],
            email = row[Users.email],
            displayName = row[Users.displayName],
            passwordHash = row[Users.passwordHash]
        )
    }

    private fun rowToTodo(row: ResultRow?): Todo? {
        if (row == null) return null
        return Todo(
            id = row[Todos.id],
            userId = row[Todos.userId],
            todo = row[Todos.todo],
            done = row[Todos.done]
        )
    }
}