package br.edu.ifpr.stiehl.aula7_sqlite.ui

import br.edu.ifpr.stiehl.aula7_sqlite.entidades.Tarefa

interface TarefaListListener {
    fun tarefaDeletada(tarefa: Tarefa)
    fun tarefaInserida(tarefa: Tarefa)
    fun tarefaUpdate(tarefa: Tarefa)
    fun buscaTarefa(id: Int):Tarefa
}