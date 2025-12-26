<template>
  <v-container fluid class="bg-grey-lighten-4 align-start">
    <v-row class="ga-4">

      <!-- Header -->
      <v-col cols="12">
        <div class="d-flex align-center mb-3">
          <v-icon icon="mdi-analytics" size="large" color="primary" class="me-3" />
          <h1 class="text-h4 font-weight-bold">Análisis Geoespacial</h1>
        </div>
        <v-divider />
      </v-col>

      <!-- 1) Tarea más cercana -->
      <v-col cols="12">
        <v-card variant="elevated" elevation="2" class="rounded-lg">
          <v-card-item>
            <template #prepend>
              <v-icon icon="mdi-target" size="x-large" color="primary" />
            </template>
            <v-card-title class="font-weight-bold">Tarea más cercana</v-card-title>
            <v-card-subtitle>Basado en tu ubicación actual</v-card-subtitle>
          </v-card-item>

          <v-divider />

          <v-card-text class="py-4">
            <div class="text-center">
              <div class="text-h5 font-weight-bold">
                {{ analysisData.nearestTask.title || 'Cargando...' }}
              </div>
              <div class="text-body-1 mt-2">
                <span class="font-weight-medium">{{ analysisData.nearestTask.distance }}</span>
                metros de distancia
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 2) Promedio de distancia (card simple) -->
      <v-col cols="12">
        <v-card variant="elevated" elevation="2" class="rounded-lg">
          <v-card-item>
            <template #prepend>
              <v-icon icon="mdi-ruler-square" color="secondary" size="x-large" />
            </template>
            <v-card-title class="font-weight-bold">Promedio de distancia de tareas completadas vs ubicacion en tiempo real</v-card-title>
            <v-card-subtitle>Tareas completadas vs Usuario</v-card-subtitle>
          </v-card-item>

          <v-divider />
          <v-card-text class="text-center py-4">
          <div
            class="text-h3 font-weight-black"
            :class="avgRegistroDistanceStats ? 'text-secondary' : 'text-medium-emphasis'"
            >
              {{
              avgRegistroDistanceStats
               ? formatMeters(avgRegistroDistanceStats.promedioDistanciaMetros)
                : 'N/A'
                   }}
         </div>
</v-card-text>


        </v-card>
      </v-col>

      <!-- 3) Ubicación registrada -->
      <v-col cols="12">
        <v-card variant="elevated" elevation="2" class="rounded-lg">
          <v-card-item>
            <template #prepend>
              <v-icon icon="mdi-account-location" color="info" size="x-large" />
            </template>
            <v-card-title class="font-weight-bold">Ubicación Registrada</v-card-title>
            <v-card-subtitle>Coordenadas PostGIS</v-card-subtitle>
          </v-card-item>

          <v-divider />

          <v-card-text class="py-4">
            <div class="text-center">
              <div class="text-body-1 font-italic">
                Lat: {{ userCoords.lat }} / Lng: {{ userCoords.lng }}
              </div>

              <div class="d-flex justify-center mt-3">
                <v-btn
                  variant="tonal"
                  color="info"
                  prepend-icon="mdi-map-marker"
                  @click="openLocationDialog"
                >
                  Cambiar en mapa
                </v-btn>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Popup: Seleccionar ubicación (igual que antes) -->
      <v-col cols="12">
        <v-dialog v-model="locationDialog" max-width="720">
          <v-card>
            <v-card-title class="text-subtitle-1 font-weight-bold">
              Selecciona tu ubicación
            </v-card-title>
            <v-card-subtitle>
              Click o arrastra el marcador para ajustar latitud/longitud.
            </v-card-subtitle>

            <v-card-text>
              <div class="leaflet-map" ref="locationMapEl" />

              <v-row class="mt-3" dense>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="locationDraft.lat"
                    label="Latitud"
                    density="comfortable"
                    variant="outlined"
                  />
                </v-col>
                <v-col cols="12" sm="6">
                  <v-text-field
                    v-model="locationDraft.lng"
                    label="Longitud"
                    density="comfortable"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
            </v-card-text>

            <v-card-actions>
              <v-spacer />
              <v-btn variant="text" :disabled="savingLocation" @click="closeLocationDialog">
                Cancelar
              </v-btn>
              <v-btn color="primary" variant="flat" :loading="savingLocation" @click="saveLocation">
                Guardar
              </v-btn>
            </v-card-actions>

            <v-alert
              v-if="locationError"
              type="error"
              variant="tonal"
              density="compact"
              class="mx-4 mb-4"
            >
              {{ locationError }}
            </v-alert>
          </v-card>
        </v-dialog>
      </v-col>

      <!-- 4) Card: promedio distancia tareas completadas -->
      <v-col cols="12">
        <v-card border flat class="rounded-lg">
          <v-card-item>
            <template #prepend>
              <v-icon icon="mdi-map-clock" color="secondary" size="x-large" />
            </template>
            <v-card-title class="font-weight-bold">
              Promedio distancia ubicacion inicial vs tareas completadas.
            </v-card-title>
            <v-card-subtitle>
              Distancia promedio entre tu ubicacion inicialmente registrada y las tareas completadas.
            </v-card-subtitle>
          </v-card-item>

          <v-divider />

          <v-card-text class="py-4">
            <div class="d-flex justify-space-between align-center mb-2">
              <v-chip v-if="avgDistanceStats" color="secondary" variant="tonal" size="small">
                {{ avgDistanceStats.username || 'Usuario' }}
              </v-chip>
            </div>

            <div class="text-center">
              <div
                class="text-h4 font-weight-black"
                :class="avgDistanceStats ? 'text-secondary' : 'text-medium-emphasis'"
              >
                {{ avgDistanceStats ? formatMeters(avgDistanceStats.promedioDistanciaMetros) : 'N/A' }}
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 6) Card: Top sector (2km) -->
      <v-col cols="12" md="5">
        <v-card border flat>
          <v-card-item title="Top sector (2km)">
            <template v-slot:subtitle>
              Sector con más tareas completadas dentro de 2km
            </template>
          </v-card-item>

          <v-card-text class="pt-0">
            <div v-if="topCompletedSector2km" class="d-flex align-start ga-3">
              <v-avatar color="green-lighten-4" size="40" class="mt-1">
                <v-icon icon="mdi-check-decagram" color="green-darken-2" />
              </v-avatar>

              <div class="flex-1">
                <div class="text-h6 font-weight-bold">
                  {{ topCompletedSector2km.sectorName || 'Sector' }}
                </div>
                <div class="text-body-2 text-medium-emphasis">
                  {{ topCompletedSector2km.completedCount ?? 0 }} tareas completadas
                </div>
              </div>

              <v-chip color="success" variant="tonal" size="small">
                +{{ topCompletedSector2km.completedCount ?? 0 }}
              </v-chip>
            </div>
            <div v-else class="text-body-2 text-medium-emphasis">
              Sin datos para mostrar.
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="5">
        <v-card border flat>
          <v-card-item title="Zonas de Concentración">
            <template #subtitle>
              Sectores con más tareas pendientes 
            </template>
          </v-card-item>

          <v-expansion-panels variant="accordion" class="pa-2">
            <v-expansion-panel
              v-for="sector in zonasConcentracion"
              :key="sector.idSector"
              elevation="0"
              class="border-thin"
            >
              <v-expansion-panel-title @click.stop="loadDetalleSector(sector)">
                <div class="d-flex align-center ga-3">
                  <span class="font-weight-medium">
                    {{ sector.sectorName }}
                  </span>

                  <v-badge
                    :content="sector.pendingCount"
                    color="success"
                    inline
                    class="ms-2"
                  />
                </div>
              </v-expansion-panel-title>

              <v-expansion-panel-text :key="sector.loaded + '-' + sector.tasks.length">
                <v-progress-circular
                  v-if="loadingSectorId === sector.idSector && !sector.loaded"
                  indeterminate
                  size="24"
                />

                <div
                  v-else-if="sector.loaded && sector.tasks.length === 0"
                  class="text-body-2 text-medium-emphasis"
                >
                  No hay tareas pendientes en este sector.
                </div>

                <v-list v-else density="compact">
                  <v-list-item
                    v-for="task in sector.tasks.slice(0, 5)"
                    :key="task.id"
                    :title="task.title"
                    prepend-icon="mdi-alert-circle-outline"
                  />
                </v-list>

              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>
       </v-card>
      </v-col>
      <v-col cols="12" md="5">
        <v-card border flat>
          <v-card-item title="Zonas de Concentración">
            <template v-slot:subtitle>
              Sectores con más tareas terminadas
            </template>
          </v-card-item>

          <v-expansion-panels variant="accordion" class="pa-2">
            <v-expansion-panel v-for="cluster in analysisData.clusters" :key="cluster.sectorName" elevation="0"
              class="border-thin">
              <v-expansion-panel-title>
                <v-badge :content="cluster.pendingCount" color="success" inline>
                  <span class="font-weight-medium">{{ cluster.sectorName }}</span>
                </v-badge>
              </v-expansion-panel-title>

              <v-expansion-panel-text>
                <v-list density="compact">
                  <v-list-item v-for="task in cluster.tasks" :key="task.id" :title="task.title"
                    prepend-icon="mdi-alert-circle-outline" />
                </v-list>
              </v-expansion-panel-text>
            </v-expansion-panel>
          </v-expansion-panels>
        </v-card>
      </v-col>

    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import analysis from '@/services/analysis'

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

// ESTADOS
const userCoords = ref({ lat: -33.4489, lng: -70.6693 }) // fallback

// Dialog + Leaflet (ubicación)
const locationDialog = ref(false)
const locationMapEl = ref(null)
const locationDraft = ref({ lat: -33.4489, lng: -70.6693 })
const savingLocation = ref(false)
const locationError = ref('')
let locationMap = null
let locationMarker = null

// Nuevo: promedio de distancia entre ubicación REGISTRADA y tareas completadas
const avgRegistroDistanceStats = ref(null)


// DATA PARA RESPONDER PREGUNTAS
const analysisData = ref({
  nearestTask: { title: '', distance: 0 }, // Pregunta: Tarea más cercana
  avgDistance: 0,                         // Pregunta: Promedio de distancia
  sectorsByRadius: [],                   // Pregunta: Sector con más tareas en radio X
  clusters: []                           // Pregunta: Concentración de tareas pendientes
})

// Nuevo: promedio de distancia tareas completadas (endpoint /promedios/distancia-tareas-completadas)
const avgDistanceStats = ref(null)

// Nuevo: primer elemento del endpoint /sectores/tareas-completadas/radio-2km
const topCompletedSector2km = ref(null)

/*// 
//Inicial
MÉTODOS PARA LLAMAR A SPRING
const loadAnalysis = async () => {
  try {
    // Ejemplo de llamadas al backend (ajusta las URLs a tus endpoints de Spring)
    // const res = await axios.get('/api/analysis/summary')
    // analysisData.value = res.data
    
    // MOCK DATA
    analysisData.value.nearestTask = { title: 'Reparación Semáforo Alameda', distance: 150 }
    analysisData.value.avgDistance = 3.4
    analysisData.value.clusters = [
      { 
        sectorName: 'Sector Centro', 
        pendingCount: 12, 
        tasks: [{ id: 1, title: 'Bacheo calle 1' }, { id: 2, title: 'Luminaria Plaza' }] 
      },
      { 
        sectorName: 'Sector Norte', 
        pendingCount: 5, 
        tasks: [{ id: 3, title: 'Limpieza canal' }] 
      }
    ]
    loadRadiusStats()
  } catch (error) {
    console.error("Error cargando análisis:", error)
  }
}*/

// MÉTODOS PARA LLAMAR A SPRING
const loadAnalysis = async () => {
  // 1) Tarea más cercana
  try {
    const res = await analysis.getTareaMasCercana()
    const data = res?.data ?? null
    console.log('Tarea más cercana:', data)
    console.log("STATUS",res.status);

    const titulo = data?.titulo ?? data?.title ?? null
    const distanciaNum = Number(data?.distancia)

    analysisData.value.nearestTask = {
      title: titulo && String(titulo).trim() ? String(titulo) : 'N/A',
      distance: Number.isFinite(distanciaNum) ? Math.round(distanciaNum) : 0,
    }
  } catch (error) {
    console.error('Error cargando tarea más cercana:', error)
    analysisData.value.nearestTask = { title: 'N/A', distance: 0 }
  }

  // 2) Promedio (Ubicacion real, se actualiza mediante el mapa)
  try {
    const res = await analysis.getPromedioUbicacionTareasRegistro()
    const data = res?.data ?? null


    if (data && typeof data === 'object') {
      const rawVal =
        data.promedioUbicacionRegistro ??   
        data.promedioDistanciaMetros ??
        data.promedio_metros ??
        data.promedio ??
        data.distanciaPromedio ??
        null


      const val = rawVal === '' || rawVal === undefined ? null : rawVal

      avgRegistroDistanceStats.value = {
        username: data.username ?? data.usuario ?? null,
        promedioDistanciaMetros: val === null ? null : Number(val),
        _raw: data,
      }
    } else {
      avgRegistroDistanceStats.value = null
    }
  } catch (error) {
    console.error('Error cargando promedio ubicación registrada vs tareas:', error)
    avgRegistroDistanceStats.value = null
  }

  // 3) Promedio (ubicacion inicial, registrada al momento de crear usuario)
  try {
    const res = await analysis.getPromedioDistanciaTareasCompletadas()
    const data = res?.data ?? null


    if (data && typeof data === 'object') {
      const rawVal = data.promedioDistanciaMetros ?? null
      const val = rawVal === '' || rawVal === undefined ? null : rawVal

      avgDistanceStats.value = {
        username: data.username ?? data.usuario ?? null,
        promedioDistanciaMetros: val === null ? null : Number(val),
        _raw: data,
      }
    } else {
      avgDistanceStats.value = null
    }
  } catch (error) {
    console.error('Error cargando promedio distancia tareas completadas:', error)
    avgDistanceStats.value = null
  }

  // 4) Top sector 2km
  try {
    const res = await analysis.getSectoresTareasCompletadasRadio2km()
    const list = Array.isArray(res?.data) ? res.data : []
    const first = list[0] ?? null

    topCompletedSector2km.value = first
      ? {
          sectorName: first.sector ?? first.sectorName ?? first.nombreSector ?? first.nombre ?? null,
          completedCount:
            first.totalCompletadas ??
            first.completedCount ??
            first.completadas ??
            first.cantidad ??
            first.total ??
            0,
          _raw: first,
        }
      : null
  } catch (error) {
    console.error('Error cargando sectores completados (2km):', error)
    topCompletedSector2km.value = null
  }

  // 5) Clusters (tu lógica actual)
  try {
    const res = await analysis.getCantidadTareasPorSector()
    if (Array.isArray(res.data)) {
      analysisData.value.clusters = res.data.map((s, index) => ({
        sectorName: s.sector || `Sector ${index + 1}`,
        pendingCount: s.cantidadTareas || 0,
        tasks: [{ id: index + 1, title: `Tareas terminadas en ${s.sector || 'este sector'}` }],
      }))
    } else {
      analysisData.value.clusters = []
    }
  } catch (error) {
    console.error('Error cargando análisis desde el servidor:', error)
    analysisData.value.clusters = []
  }
}




const formatMeters = (value) => {
  if (value === null || value === undefined || Number.isNaN(Number(value))) return 'N/A'
  const meters = Number(value)
  if (meters >= 1000) return `${(meters / 1000).toFixed(2)} km`
  return `${Math.round(meters)} m`
}

const openLocationDialog = async () => {
  locationError.value = ''
  locationDraft.value = {
    lat: Number(userCoords.value.lat),
    lng: Number(userCoords.value.lng),
  }
  locationDialog.value = true
  await nextTick()
  initLocationMap()
}

const closeLocationDialog = () => {
  if (savingLocation.value) return
  locationDialog.value = false
}

const initLocationMap = () => {
  if (locationMap || !locationMapEl.value) return

  const start = [Number(locationDraft.value.lat), Number(locationDraft.value.lng)]
  locationMap = L.map(locationMapEl.value, { zoomControl: true }).setView(start, 13)

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors',
  }).addTo(locationMap)

  locationMarker = L.marker(start, { draggable: true }).addTo(locationMap)

  locationMap.on('click', (e) => {
    locationDraft.value.lat = Number(e.latlng.lat.toFixed(6))
    locationDraft.value.lng = Number(e.latlng.lng.toFixed(6))
  })

  locationMarker.on('dragend', () => {
    const ll = locationMarker.getLatLng()
    locationDraft.value.lat = Number(ll.lat.toFixed(6))
    locationDraft.value.lng = Number(ll.lng.toFixed(6))
  })

  // en dialogs, Leaflet necesita refrescar tamaño
  setTimeout(() => {
    locationMap?.invalidateSize?.()
  }, 0)
}

const destroyLocationMap = () => {
  if (locationMap) {
    locationMap.remove()
    locationMap = null
    locationMarker = null
  }
}

watch(locationDialog, (open) => {
  if (!open) destroyLocationMap()
})

watch(
  () => [locationDraft.value.lat, locationDraft.value.lng],
  () => {
    if (!locationMap || !locationMarker) return
    const lat = Number(locationDraft.value.lat)
    const lng = Number(locationDraft.value.lng)
    if (Number.isNaN(lat) || Number.isNaN(lng)) return
    locationMarker.setLatLng([lat, lng])
    locationMap.panTo([lat, lng], { animate: true })
  }
)

const saveLocation = async () => {
  const lat = Number(locationDraft.value.lat)
  const lng = Number(locationDraft.value.lng)
  if (Number.isNaN(lat) || Number.isNaN(lng)) {
    locationError.value = 'Latitud/Longitud inválidas.'
    return
  }

  savingLocation.value = true
  locationError.value = ''
  try {
    // Backend espera: { latitud, longitud }
    await analysis.UpdatePosicionUsuarioTiempoReal({
      latitud: Number(lat.toFixed(6)),
      longitud: Number(lng.toFixed(6)),
    })

    // si el backend responde OK, reflejamos en UI
    userCoords.value = {
      lat: Number(lat.toFixed(6)),
      lng: Number(lng.toFixed(6)),
    }

    // persistencia local como fallback
    try {
      localStorage.setItem('userCoords', JSON.stringify(userCoords.value))
    } catch {
      // ignore
    }

    locationDialog.value = false
  } catch (err) {
    console.error('Error actualizando posición en tiempo real:', err)
    locationError.value = 'No se pudo actualizar tu ubicación. Intenta nuevamente.'
  } finally {
    savingLocation.value = false
  }
}

const zonasConcentracion = ref([])        
const loadingSectorId = ref(null)        

const loadZonasConcentracion = async () => {
  try {
    const res = await analysis.getTareasPendientesSector()

    zonasConcentracion.value = Array.isArray(res.data)
    ? res.data.map(s => ({
        idSector: s.id,
        sectorName: s.nombre,
        pendingCount: s.pendientes,
        tasks: [],          
        loaded: false
      }))
    : []


  } catch (err) {
    console.error("Error cargando zonas de concentración:", err)
    zonasConcentracion.value = []
  }
}

const loadDetalleSector = async (sector) => {
  if (sector.loaded || loadingSectorId.value === sector.idSector) return

  loadingSectorId.value = sector.idSector
  try {
    const res = await analysis.getTareasPendientesSectorDetallado(sector.idSector)

    const tasks = Array.isArray(res.data)
      ? res.data.map(t => ({ id: t.id, title: t.titulo }))
      : []

    sector.tasks = tasks
    sector.loaded = true
    zonasConcentracion.value = [...zonasConcentracion.value] 
  } catch (err) {
    console.error("Error cargando detalle sector:", err)
    sector.tasks = []
    sector.loaded = true
    zonasConcentracion.value = [...zonasConcentracion.value] 
  } finally {
    loadingSectorId.value = null
  }
}

onMounted(() => {
  // cargar último valor si existe
  try {
    const saved = localStorage.getItem('userCoords')
    if (saved) {
      const parsed = JSON.parse(saved)
      const lat = Number(parsed?.lat)
      const lng = Number(parsed?.lng)
      if (!Number.isNaN(lat) && !Number.isNaN(lng)) {
        userCoords.value = { lat, lng }
      }
    }
  } catch {
    // ignore
  }

  loadAnalysis()
  loadZonasConcentracion()
})


</script>

<script>
export default { name: 'AnalysisNearestView' }
</script>

<style scoped>
.border-thin {
  border-bottom: 1px solid #e0e0e0 !important;
}

.leaflet-map {
  height: 320px;
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
}

@media (max-width: 600px) {
  .leaflet-map {
    height: 260px;
  }
}
.border-thin {
  border-bottom: 1px solid #e0e0e0 !important;
}

.leaflet-map {
  height: 320px;
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
}

@media (max-width: 600px) {
  .leaflet-map {
    height: 260px;
  }
}

/* opcional: hace que las cards se vean más consistentes */
.rounded-lg {
  border-radius: 16px;
}

</style>