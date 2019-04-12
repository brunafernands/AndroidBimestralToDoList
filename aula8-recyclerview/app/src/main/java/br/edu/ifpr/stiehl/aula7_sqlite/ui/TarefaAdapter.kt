package br.edu.ifpr.stiehl.aula7_sqlite.ui

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.ifpr.stiehl.aula7_sqlite.R
import br.edu.ifpr.stiehl.aula7_sqlite.R.id.btSalvarTarefaCriada
import br.edu.ifpr.stiehl.aula7_sqlite.bd.dao.TarefaDao
import br.edu.ifpr.stiehl.aula7_sqlite.entidades.Tarefa
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_addtarefa.view.*
import kotlinx.android.synthetic.main.item_tarefa.view.*

class TarefaAdapter(
    private var tarefas: MutableList<Tarefa>,
    private var listener: TarefaListListener,
    private var dao: TarefaDao
) :
    RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder>() {

    private var tarefasEditando: MutableList<Tarefa> = mutableListOf()

    fun adicionarTarefaEditar(tarefa: Tarefa) {
        tarefasEditando.add(0, tarefa)
    }

    fun adicionarTarefa(tarefa: Tarefa) {
        tarefas.add(0, tarefa)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TarefaViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(viewType, parent, false)
        return TarefaViewHolder(view)
    }

    override fun getItemCount() = tarefas.size

    override fun getItemViewType(position: Int): Int {
        val tarefa = tarefas[position]

        return if (tarefasEditando.contains(tarefa))
            R.layout.item_addtarefa
        else
            R.layout.item_tarefa
    }

    override fun onBindViewHolder(holder: TarefaViewHolder, position: Int) {
        val tarefa = tarefas[position]
        holder.preencherView(tarefa)
    }

    inner class TarefaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun preencherView(tarefa: Tarefa) {

            itemView.setOnLongClickListener {
                if (tarefa.status == true) {
                    itemView.cbStatus.isChecked = false
                    tarefa.status = false
                } else {
                    itemView.cbStatus.isChecked = true
                    tarefa.status = true
                }

                with(this@TarefaAdapter) {
                    listener.tarefaUpdate(tarefa)
                    val posicao = tarefasEditando.indexOf(tarefa)
                    notifyItemChanged(posicao)
                }

                true
            }

            if (tarefasEditando.contains(tarefa)) {
                itemView.plainTextDesc.setText(tarefa.desc)
                itemView.plainTextTitulo.setText(tarefa.titulo)

                //BOTAO DE SALVAR
                itemView.btSalvarTarefaCriada.setOnClickListener {
                    with(this@TarefaAdapter) {
                        tarefa.titulo = itemView.plainTextTitulo.text.toString()
                        tarefa.desc = itemView.plainTextDesc.text.toString()
//                        if(tarefa.id==0){
//                            tarefa.id=1
//                        }
                        val buscaTarefa = listener.buscaTarefa(tarefa.id)
                        if(buscaTarefa==null){
                            listener.tarefaInserida(tarefa)
                        }else{
                            listener.tarefaUpdate(tarefa)
                        }
                        val posicao = tarefasEditando.indexOf(tarefa)
                        tarefasEditando.removeAt(posicao)
                        notifyItemChanged(posicao)
                    }

                }

            } else {
                itemView.txtTitulo.setText(tarefa.titulo)
                itemView.txtDesc.setText(tarefa.desc)
                itemView.cbStatus.isChecked = tarefa.status


                //BOTAO DE COMPARTILHAR
                itemView.btShare.setOnClickListener {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    with(shareIntent) {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_SUBJECT,
                            "Compartilhar - Oba, acabei de concluir a tarefa " + itemView.txtTitulo + "!"
                        )
                        putExtra(Intent.EXTRA_TEXT, "Obaaa, acabei de concluir a tarefa " +  itemView.txtTitulo.text.toString() + "!")

                        shareIntent.setType("text/plain")
                        shareIntent.putExtra(
                            Intent.EXTRA_SUBJECT,
                            "Obaaa, acabei de concluir a tarefa " + itemView.txtTitulo + "!"
                        )
                    }

                    itemView.context.startActivity(shareIntent)

                }

                //BOTAO DE EXCLUIR
                itemView.btExcluir.setOnClickListener {
                    with(this@TarefaAdapter) {
                        val posicao = tarefas.indexOf(tarefa)
                        tarefas.removeAt(posicao)
                        notifyItemRemoved(posicao)
                        listener.tarefaDeletada(tarefa)

                    }

                }

                //BOTAO DE EDITAR
                itemView.btEdit.setOnClickListener {
                    with(this@TarefaAdapter) {

                        tarefasEditando.add(tarefa)
                        val posicao = tarefas.indexOf(tarefa)
                        notifyItemChanged(posicao)


                        }
                    }
                }
            }
        }
    }