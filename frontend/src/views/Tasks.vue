<template>
  <v-container>

    <!-- HEADER -->
    <div class="d-flex align-center mb-4 position-relative">
      <!-- Notificaciones -->
      <v-menu location="bottom start">
        <template #activator="{ props }">
          <v-badge
            :content="notificaciones.length"
            :model-value="notificaciones.length > 0"
            color="error"
            overlap
          >
            <v-btn icon v-bind="props" variant="text">
              <v-icon>mdi-bell</v-icon>
            </v-btn>
          </v-badge>
        </template>

        <v-card min-width="300">
          <v-card-title class="text-subtitle-1">Notificaciones</v-card-title>
          <v-divider />
          <v-list density="compact">
            <v-list-item
              v-for="(n, i) in notificaciones"
              :key="i"
            >
              <v-list-item-title>{{ n.titulo }}</v-list-item-title>
              <v-list-item-subtitle>
                Vence el {{ n.fechaVencimiento }}
              </v-list-item-subtitle>
            </v-list-item>

            <v-list-item v-if="notificaciones.length === 0">
              <v-list-item-title class="text-medium-emphasis">
                No hay tareas próximas
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </v-menu>

      <h1 class="text-h5 mx-auto">Mis Tareas</h1>

      <v-btn
        color="primary"
        prepend-icon="mdi-plus"
        @click="dialog = true"
      >
        Nueva
      </v-btn>
    </div>

    <!-- BUSCADOR -->
    <v-text-field
      v-model="search"
      label="Buscar tarea..."
      prepend-inner-icon="mdi-magnify"
      variant="outlined"
      hide-details
      class="mb-4"
    />

    <!-- LISTA VERTICAL (SIN SCROLL INTERNO) -->
    <v-alert
      v-if="errorMessage"
      type="error"
      variant="tonal"
      class="mb-3"
    >
      {{ errorMessage }}
    </v-alert>

    <v-list density="comfortable" lines="two">

      <v-list-item
        v-for="task in filteredTasks"
        :key="task.id"
        class="mb-3"
      >
        <v-card variant="outlined" class="pa-3">

          <!-- Título + estado -->
          <div class="d-flex justify-space-between align-center mb-1">
            <div class="task-title">
              {{ task.title }}
            </div>

            <v-chip
              :color="task.completed ? 'success' : 'warning'"
              size="small"
            >
              {{ task.completed ? 'Completada' : 'Pendiente' }}
            </v-chip>
          </div>

          <!-- Descripción -->
          <div class="task-desc mb-2">
            {{ task.description || 'Sin descripción' }}
          </div>

          <!-- Meta -->
          <div class="text-caption text-medium-emphasis mb-2">
            📅 {{ task.fechaVencimiento || 'Sin vencimiento' }} ·
            🏷 Sector: {{ task.idSector ?? '-' }}
          </div>

          <!-- Ubicación -->
          <div class="text-caption mb-2">
            <v-icon size="small" color="red">mdi-map-marker</v-icon>
            <span v-if="task.latitud != null && task.longitud != null">
              {{ Number(task.latitud).toFixed(4) }},
              {{ Number(task.longitud).toFixed(4) }}
            </span>
            <span v-else>-</span>
          </div>

          <!-- Acciones -->
          <div class="d-flex justify-end">
            <v-btn
              icon
              variant="text"
              size="small"
              color="blue"
              @click="editTask(task)"
            >
              <v-icon>mdi-pencil</v-icon>
            </v-btn>

            <v-btn
              icon
              variant="text"
              size="small"
              color="error"
              @click="deleteTask(task.id)"
            >
              <v-icon>mdi-delete</v-icon>
            </v-btn>
          </div>

        </v-card>
      </v-list-item>

      <v-list-item v-if="filteredTasks.length === 0">
        <v-list-item-title class="text-medium-emphasis">
          No hay tareas para mostrar
        </v-list-item-title>
      </v-list-item>

    </v-list>

    <!-- DIALOGOS -->
   <!-- DIALOG: CREAR TAREA -->
<v-dialog v-model="dialog" max-width="520" attach="body">
  <v-card>
    <v-card-title class="text-h6 font-weight-bold">Crear Tarea</v-card-title>
    <v-card-subtitle>Completa los datos y selecciona ubicación en el mapa.</v-card-subtitle>

    <v-card-text>
      <v-alert
        v-if="dialogErrorMessage"
        type="error"
        variant="tonal"
        density="comfortable"
        class="mb-4"
      >
        {{ dialogErrorMessage }}
      </v-alert>

      <v-text-field
        v-model="newTask.title"
        label="Título"
        variant="outlined"
        required
        class="mb-3"
      />

      <v-textarea
        v-model="newTask.description"
        label="Descripción"
        variant="outlined"
        auto-grow
        rows="2"
        class="mb-3"
      />

      <v-row dense class="mb-1">
        <v-col cols="12" sm="6">
          <v-text-field
            v-model="newTask.fechaVencimiento"
            label="Vencimiento"
            type="date"
            variant="outlined"
            density="comfortable"
          />
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="newTask.idSector"
            label="ID Sector"
            type="number"
            variant="outlined"
            density="comfortable"
          />
        </v-col>
      </v-row>

      <!-- TOGGLE COMPLETADA -->
      <v-switch
        v-model="newTask.completed"
        :label="newTask.completed ? 'Completada' : 'Pendiente'"
        color="success"
        inset
        hide-details
        class="mb-3"
      />

      <v-row dense class="mb-2">
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="newTask.latitud"
            label="Latitud"
            type="number"
            variant="outlined"
            density="comfortable"
          />
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="newTask.longitud"
            label="Longitud"
            type="number"
            variant="outlined"
            density="comfortable"
          />
        </v-col>
      </v-row>

      <div class="map-wrap mb-2 border-thin elevation-4 rounded-lg">
        <div class="text-caption text-medium-emphasis mb-2">
          Selecciona la ubicación en el mapa (click) o arrastra el marcador.
        </div>
        <div ref="mapEl" class="leaflet-map" />
      </div>
    </v-card-text>

    <v-card-actions>
      <v-spacer />
      <v-btn variant="text" @click="dialog = false">Cancelar</v-btn>
      <v-btn color="primary" :loading="dialogLoading" @click="createTask">
        Guardar
      </v-btn>
    </v-card-actions>
  </v-card>
</v-dialog>

<!-- DIALOG: EDITAR TAREA -->
<v-dialog v-model="editDialog" max-width="520" attach="body">
  <v-card>
    <v-card-title class="text-h6 font-weight-bold">Editar Tarea</v-card-title>
    <v-card-subtitle>Modifica los datos y ajusta la ubicación si lo necesitas.</v-card-subtitle>

    <v-card-text>
      <v-alert
        v-if="editDialogErrorMessage"
        type="error"
        variant="tonal"
        density="comfortable"
        class="mb-4"
      >
        {{ editDialogErrorMessage }}
      </v-alert>

      <v-text-field
        v-model="editTaskForm.title"
        label="Título"
        variant="outlined"
        required
        class="mb-3"
      />

      <v-textarea
        v-model="editTaskForm.description"
        label="Descripción"
        variant="outlined"
        auto-grow
        rows="2"
        class="mb-3"
      />

      <v-row dense class="mb-1">
        <v-col cols="12" sm="6">
          <v-text-field
            v-model="editTaskForm.fechaVencimiento"
            label="Vencimiento"
            type="date"
            variant="outlined"
            density="comfortable"
          />
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="editTaskForm.idSector"
            label="ID Sector"
            type="number"
            variant="outlined"
            density="comfortable"
          />
        </v-col>
      </v-row>

      <!-- TOGGLE COMPLETADA -->
      <v-switch
        v-model="editTaskForm.completed"
        :label="editTaskForm.completed ? 'Completada' : 'Pendiente'"
        color="success"
        inset
        hide-details
        class="mb-3"
      />

      <v-row dense class="mb-2">
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="editTaskForm.latitud"
            label="Latitud"
            type="number"
            variant="outlined"
            density="comfortable"
          />
        </v-col>
        <v-col cols="12" sm="6">
          <v-text-field
            v-model.number="editTaskForm.longitud"
            label="Longitud"
            type="number"
            variant="outlined"
            density="comfortable"
          />
        </v-col>
      </v-row>

      <div class="map-wrap mb-2 border-thin elevation-4 rounded-lg">
        <div class="text-caption text-medium-emphasis mb-2">
          Selecciona la ubicación en el mapa (click) o arrastra el marcador.
        </div>
        <div ref="editMapEl" class="leaflet-map" />
      </div>
    </v-card-text>

    <v-card-actions>
      <v-spacer />
      <v-btn variant="text" @click="editDialog = false">Cancelar</v-btn>
      <v-btn color="primary" :loading="editDialogLoading" @click="updateTask">
        Guardar cambios
      </v-btn>
    </v-card-actions>
  </v-card>
</v-dialog>



  </v-container>
</template>


<script>
import tareas from '@/services/tareas';
import L from 'leaflet'

import 'leaflet/dist/leaflet.css'

// Fix íconos en bundlers (webpack) para que el marker aparezca correctamente
import markerIcon2x from 'leaflet/dist/images/marker-icon-2x.png'
import markerIcon from 'leaflet/dist/images/marker-icon.png'
import markerShadow from 'leaflet/dist/images/marker-shadow.png'

delete L.Icon.Default.prototype._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
})

export default {
  name: 'TasksView',
  data() {
    return {
      dialog: false,
      dialogLoading: false,
      dialogErrorMessage: '',
      editDialog: false,
      editDialogLoading: false,
      editDialogErrorMessage: '',
      newTask: {
        title: '',
        description: '',
        fechaVencimiento: '',
        idSector: null,
        completed: false,
        latitud: -33.4489,
        longitud: -70.6482,
      },
      editTaskForm: {
        id: null,
        title: '',
        description: '',
        fechaVencimiento: '',
        idSector: null,
        completed: false,
        latitud: -33.4489,
        longitud: -70.6482,
      },
      search: '',
      loading: false,
      errorMessage: '',
      headers: [
        { title: 'Tarea / Descripción', key: 'title', align: 'start' },
        { title: 'Vencimiento', key: 'fechaVencimiento' },
        { title: 'Estado', key: 'completed', align: 'center' },
        { title: 'ID Sector', key: 'idSector', align: 'center' },
        { title: 'Ubicación (GPS)', key: 'location', sortable: false },
        { title: 'Acciones', key: 'actions', sortable: false, align: 'end' },
      ],
      notificaciones: [],
      tasks: [], // Empezamos con el array vacío

      // Leaflet (dialog)
      map: null,
      marker: null,

      // Leaflet (edit dialog)
      editMap: null,
      editMarker: null,
    }
  },
  watch: {
    dialog(isOpen) {
    if (isOpen) {
      this.dialogErrorMessage = ''
      this.dialogLoading = false
      this.$nextTick(() => this.initMap())
    } else {
      this.destroyMap()
    }
  },
    editDialog(isOpen) {
    if (isOpen) {
      this.editDialogErrorMessage = ''
      this.editDialogLoading = false
      this.$nextTick(() => this.initEditMap())
    } else {
      this.destroyEditMap()
    }
  },
    'newTask.latitud'() {
      this.syncMarkerFromFields()
    },
    'newTask.longitud'() {
      this.syncMarkerFromFields()
    },
    'editTaskForm.latitud'() {
      this.syncEditMarkerFromFields()
    },
    'editTaskForm.longitud'() {
      this.syncEditMarkerFromFields()
    },
  },
  mounted() {
    this.cargarTareas()
    this.cargarNotificaciones()
  },
  beforeUnmount() {
    if (this.map) {
      this.map.remove()
      this.map = null
    }
    if (this.editMap) {
      this.editMap.remove()
      this.editMap = null
    }
  },
  methods: {
    getRow(item) {
      return item?.raw ?? item
    },
    async cargarNotificaciones() {
      try {
        const res = await tareas.getNotificaciones()
        this.notificaciones = res.data || []
      } catch (e) {
        this.notificaciones = []
      }
    },
    resetNewTask() {
      this.newTask = {
        title: '',
        description: '',
        fechaVencimiento: '',
        idSector: null,
        completed: false,
        latitud: -33.4489,
        longitud: -70.6482,
      }
      
    },
    destroyMap() {
    if (this.map) {
      this.map.remove()
      this.map = null
      this.marker = null
    }
  },
  destroyEditMap() {
    if (this.editMap) {
      this.editMap.remove()
      this.editMap = null
      this.editMarker = null
    }
  },
    initMap() {
      // El dialog se monta/desmonta; si ya existe el mapa no lo recreamos.
      if (this.map || !this.$refs.mapEl) return

      const start = [Number(this.newTask.latitud), Number(this.newTask.longitud)]
      this.map = L.map(this.$refs.mapEl, {
        zoomControl: true,
      }).setView(start, 13)

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors',
      }).addTo(this.map)

      this.marker = L.marker(start, { draggable: true }).addTo(this.map)

      this.map.on('click', (e) => {
        this.newTask.latitud = Number(e.latlng.lat.toFixed(6))
        this.newTask.longitud = Number(e.latlng.lng.toFixed(6))
      })

      this.marker.on('dragend', () => {
        const ll = this.marker.getLatLng()
        this.newTask.latitud = Number(ll.lat.toFixed(6))
        this.newTask.longitud = Number(ll.lng.toFixed(6))
      })

      this.$nextTick(() => {
        try {
          this.map.invalidateSize()
        } catch (e) {
          // noop
        }
      })
    },
    syncMarkerFromFields() {
      if (!this.map || !this.marker) return
      const lat = Number(this.newTask.latitud)
      const lng = Number(this.newTask.longitud)
      if (Number.isNaN(lat) || Number.isNaN(lng)) return
      this.marker.setLatLng([lat, lng])
      this.map.panTo([lat, lng], { animate: true })
    },

    initEditMap() {
      if (this.editMap || !this.$refs.editMapEl) return

      const start = [Number(this.editTaskForm.latitud), Number(this.editTaskForm.longitud)]
      this.editMap = L.map(this.$refs.editMapEl, {
        zoomControl: true,
      }).setView(start, 13)

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors',
      }).addTo(this.editMap)

      this.editMarker = L.marker(start, { draggable: true }).addTo(this.editMap)

      this.editMap.on('click', (e) => {
        this.editTaskForm.latitud = Number(e.latlng.lat.toFixed(6))
        this.editTaskForm.longitud = Number(e.latlng.lng.toFixed(6))
      })

      this.editMarker.on('dragend', () => {
        const ll = this.editMarker.getLatLng()
        this.editTaskForm.latitud = Number(ll.lat.toFixed(6))
        this.editTaskForm.longitud = Number(ll.lng.toFixed(6))
      })

      this.$nextTick(() => {
        try {
          this.editMap.invalidateSize()
        } catch (e) {
          // noop
        }
      })
    },
    syncEditMarkerFromFields() {
      if (!this.editMap || !this.editMarker) return
      const lat = Number(this.editTaskForm.latitud)
      const lng = Number(this.editTaskForm.longitud)
      if (Number.isNaN(lat) || Number.isNaN(lng)) return
      this.editMarker.setLatLng([lat, lng])
      this.editMap.panTo([lat, lng], { animate: true })
    },
    async cargarTareas() {
      this.loading = true
      this.errorMessage = ''
      try {
        // El backend filtra por el usuario autenticado (JWT) y devuelve solo “mis tareas”.
        const response = await tareas.getTasksUser();
    
        this.tasks = (response.data || []).map(t => ({
          // mantener el id para acciones
          id: t.id,

          // backend -> ui
          title: t.titulo,
          description: t.descripcion,
          fechaVencimiento: t.fechaVencimiento,
          completed: t.completada,
          idSector: t.sector?.id ?? null,

          // coordenadas (pueden venir null)
          latitud: t.latitud ?? null,
          longitud: t.longitud ?? null,

          _raw: t,
        }))
      } catch (error) {
        console.error("Error al obtener tareas:", error)
        this.errorMessage =
          error?.response?.data?.message ||
          error?.response?.data?.error ||
          'Error al obtener tareas. Revisa que el backend esté corriendo y que estés logueado.'
      } finally {
        this.loading = false
      }
    },
    async deleteTask(id) {
      if (confirm('¿Estás seguro de eliminar esta tarea?')) {
        try {
          await tareas.deleteTask(id)
          this.cargarTareas()
        } catch (error) {
          alert("Error al eliminar")
        }
      }
    },
    addTask() {
      this.createTask()
    },

    async createTask() {
      if (!this.newTask.title) return

      this.dialogLoading = true
      this.dialogErrorMessage = ''

      // Payload backend (flexible): algunas implementaciones esperan sectorId,
      const sectorIdNumber =
        this.newTask.idSector === '' || this.newTask.idSector == null
          ? null
          : Number(this.newTask.idSector)

      const hasCoords =
        this.newTask.latitud != null &&
        this.newTask.longitud != null &&
        !Number.isNaN(Number(this.newTask.latitud)) &&
        !Number.isNaN(Number(this.newTask.longitud))

      const payload = {
        titulo: this.newTask.title,
        descripcion: this.newTask.description || null,
        fechaVencimiento: this.newTask.fechaVencimiento || null,
        completada: !!this.newTask.completed,

        ...(sectorIdNumber != null ? { idSector: sectorIdNumber } : {}),

        latitud: hasCoords ? Number(this.newTask.latitud) : 0,
        longitud: hasCoords ? Number(this.newTask.longitud) : 0,
}

      try {
        await tareas.createTask(payload)
        this.dialog = false
        this.resetNewTask()
        await this.cargarTareas()
      } catch (error) {
        console.error('Error al crear tarea:', error)
        this.dialogErrorMessage =
          error?.response?.data?.message ||
          error?.response?.data?.error ||
          'No se pudo crear la tarea.'
      } finally {
        this.dialogLoading = false
      }
    },

    async updateTask() {
      if (!this.editTaskForm?.id) return
      if (!this.editTaskForm.title) return

      this.editDialogLoading = true
      this.editDialogErrorMessage = ''

      const sectorIdNumber =
        this.editTaskForm.idSector === '' || this.editTaskForm.idSector == null
          ? null
          : Number(this.editTaskForm.idSector)

      const hasCoords =
        this.editTaskForm.latitud != null &&
        this.editTaskForm.longitud != null &&
        !Number.isNaN(Number(this.editTaskForm.latitud)) &&
        !Number.isNaN(Number(this.editTaskForm.longitud))

      const payload = {
        titulo: this.editTaskForm.title,
        descripcion: this.editTaskForm.description || null,
        fechaVencimiento: this.editTaskForm.fechaVencimiento || null,
        completada: !!this.editTaskForm.completed,

        ...(sectorIdNumber != null ? { idSector: sectorIdNumber } : {}),

        latitud: hasCoords ? Number(this.editTaskForm.latitud) : 0,
        longitud: hasCoords ? Number(this.editTaskForm.longitud) : 0,
      }

      try {
        await tareas.updateTask(this.editTaskForm.id, payload)
        this.editDialog = false
        await this.cargarTareas()
      } catch (error) {
        console.error('Error al actualizar tarea:', error)
        this.editDialogErrorMessage =
          error?.response?.data?.message ||
          error?.response?.data?.error ||
          'No se pudo actualizar la tarea.'
      } finally {
        this.editDialogLoading = false
      }
    },

    editTask(row) {
      if (!row) return
      this.editTaskForm = {
        id: row.id,
        title: row.title ?? '',
        description: row.description ?? '',
        fechaVencimiento: row.fechaVencimiento ?? '',
        idSector: row.idSector ?? null,
        completed: !!row.completed,
        latitud: row.latitud ?? -33.4489,
        longitud: row.longitud ?? -70.6482,
      }

      // Si el mapa de edición ya existe, sincronizamos marker/centro.
      this.$nextTick(() => {
        if (this.editMap && this.editMarker) {
          this.syncEditMarkerFromFields()
          try {
            this.editMap.invalidateSize()
          } catch (e) {
            // noop
          }
        }
      })

      this.editDialog = true
    },
    saveEdit() {
      this.updateTask()
    },
  },
  computed: {
  filteredTasks() {
    if (!this.search) return this.tasks
    const q = this.search.toLowerCase()
    return this.tasks.filter(t =>
      t.title?.toLowerCase().includes(q) ||
      t.description?.toLowerCase().includes(q)
    )
  },
}

}
</script>

<style scoped>
.task-title {
  font-weight: 700;
  line-height: 1.2;
}

.map-wrap {
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 12px;
  padding: 12px;
  background: #fff;
}

.leaflet-map {
  width: 100%;
  height: 220px;
  border-radius: 10px;
}

@media (min-width: 600px) {
  .leaflet-map {
    height: 240px;
  }
}

/* Mobile-first: let the description wrap naturally */
.task-desc {
  color: rgba(0, 0, 0, 0.6);
  font-size: 0.75rem;
  line-height: 1.2;
  margin-top: 2px;
  white-space: normal;
  word-break: break-word;
}

/* On wider screens, keep table compact with a 2-line clamp */
@media (min-width: 960px) {
  .task-desc {
    display: -webkit-box;
    line-clamp: 2;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .task-title {
  font-weight: 600;
  font-size: 1rem;
  line-height: 1.2;
}

.task-desc {
  font-size: 0.85rem;
  color: rgba(0, 0, 0, 0.65);
  white-space: normal;
  word-break: break-word;
}

}
</style>