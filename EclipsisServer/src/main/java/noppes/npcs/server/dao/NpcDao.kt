package noppes.npcs.server.dao

import cz.neumimto.core.PersistentContext
import cz.neumimto.core.dao.GenericDao
import noppes.npcs.server.persistance.NpcModel
import org.hibernate.SessionFactory

object NpcDao : GenericDao<NpcModel>() {

    @PersistentContext("customnpcs")
    lateinit var sessionFactory: SessionFactory

    override fun getFactory(): SessionFactory {
        return this.sessionFactory
    }

}