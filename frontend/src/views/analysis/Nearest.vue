<template>
  <v-container fluid class="bg-grey-lighten-4 fill-height align-start">
    <v-row>
      <v-col cols="12">
        <div class="d-flex align-center mb-4">
          <v-icon icon="mdi-analytics" size="large" color="primary" class="me-3"></v-icon>
          <h1 class="text-h4 font-weight-bold">Análisis Geoespacial</h1>
        </div>
        <v-divider></v-divider>
      </v-col>

      <v-col cols="12" md="4">
        <v-card variant="elevated" elevation="2">
          <v-card-item>
            <template v-slot:prepend>
              <v-icon icon="mdi-target" size="x-large"></v-icon>
            </template>
            <v-card-title>Tarea más cercana</v-card-title>
            <v-card-subtitle>Basado en tu ubicación actual</v-card-subtitle>
          </v-card-item>
          <v-card-text class="text-center py-4">
            <div class="text-h4 font-weight-bold">{{ analysisData.nearestTask.title || 'Cargando...' }}</div>
            <div class="text-h6 mt-2">{{ analysisData.nearestTask.distance }} metros de distancia</div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card variant="elevated" elevation="2">
          <v-card-item>
            <template v-slot:prepend>
              <v-icon icon="mdi-ruler-square" color="secondary" size="x-large"></v-icon>
            </template>
            <v-card-title>Promedio de Distancia</v-card-title>
            <v-card-subtitle>Tareas completadas vs Usuario</v-card-subtitle>
          </v-card-item>
          <v-card-text class="text-center py-4">
            <div class="text-h3 font-weight-black text-secondary">
              {{ analysisData.avgDistance }} <span class="text-h5">km</span>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card variant="elevated" elevation="2">
          <v-card-item>
            <template v-slot:prepend>
              <v-icon icon="mdi-account-location" color="info" size="x-large"></v-icon>
            </template>
            <v-card-title>Ubicación Registrada</v-card-title>
            <v-card-subtitle>Coordenadas PostGIS</v-card-subtitle>
          </v-card-item>
          <v-card-text class="text-center py-4">
            <div class="text-body-1 font-italic">
              Lat: {{ userCoords.lat }} / Lng: {{ userCoords.lng }}
            </div>
            <v-btn variant="text" color="info" size="small" prepend-icon="mdi-refresh">Actualizar</v-btn>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="7">
        <v-card border flat>
          <v-toolbar flat color="white">
            <v-toolbar-title class="font-weight-bold">Sectores con más éxito</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn-toggle
              v-model="selectedRadius"
              mandatory
              color="primary"
              variant="outlined"
              density="comfortable"
              @update:model-value="loadRadiusStats"
            >
              <v-btn value="2">2 KM</v-btn>
              <v-btn value="5">5 KM</v-btn>
            </v-btn-toggle>
          </v-toolbar>
          
          <v-divider></v-divider>

          <v-list lines="three" class="pa-4">
            <v-list-item v-for="sector in analysisData.sectorsByRadius" :key="sector.id">
              <template v-slot:prepend>
                <v-avatar color="green-lighten-4">
                  <v-icon icon="mdi-office-building-marker" color="green-darken-2"></v-icon>
                </v-avatar>
              </template>
              <v-list-item-title class="text-h6 font-weight-bold">{{ sector.name }}</v-list-item-title>
              <v-list-item-subtitle>
                {{ sector.completed }} tareas completadas en este radio.
                <v-progress-linear
                  class="mt-2"
                  :model-value="sector.percentage"
                  color="green"
                  height="10"
                  rounded
                ></v-progress-linear>
              </v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>

      <v-col cols="12" md="5">
        <v-card border flat>
          <v-card-item title="Zonas de Concentración">
            <template v-slot:subtitle>
              Sectores con más tareas pendientes (Clustering)
            </template>
          </v-card-item>
          
          <v-expansion-panels variant="accordion" class="pa-2">
            <v-expansion-panel
              v-for="cluster in analysisData.clusters"
              :key="cluster.sectorName"
              elevation="0"
              class="border-thin"
            >
              <v-expansion-panel-title>
                <v-badge :content="cluster.pendingCount" color="error" inline>
                  <span class="font-weight-medium">{{ cluster.sectorName }}</span>
                </v-badge>
              </v-expansion-panel-title>
              <v-expansion-panel-text>
                <v-list density="compact">
                  <v-list-item
                    v-for="task in cluster.tasks"
                    :key="task.id"
                    :title="task.title"
                    prepend-icon="mdi-alert-circle-outline"
                  >
                    <template v-slot:append>
                      <v-btn size="x-small" icon="mdi-eye" variant="text"></v-btn>
                    </template>
                  </v-list-item>
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
import { ref, onMounted } from 'vue'
import analysis from '@/services/analysis'

// ESTADOS
const selectedRadius = ref('2')
const userCoords = ref({ lat: -33.4489, lng: -70.6693 }) // Ejemplo Santiago

// DATA PARA RESPONDER PREGUNTAS
const analysisData = ref({
  nearestTask: { title: '', distance: 0 }, // Pregunta: Tarea más cercana
  avgDistance: 0,                         // Pregunta: Promedio de distancia
  sectorsByRadius: [],                   // Pregunta: Sector con más tareas en radio X
  clusters: []                           // Pregunta: Concentración de tareas pendientes
})

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
  try {
    const res = await analysis.getTareaMasCercana()

    if (res && res.data) {
      analysisData.value.nearestTask = {
        title: res.data.titulo,
        distance: Math.round(Number(res.data.distancia))
      }
    } else {
      analysisData.value.nearestTask = { title: 'N/A', distance: 0 }
    }

  } catch (error) {
    console.error("Error cargando tarea más cercana:", error)
    analysisData.value.nearestTask = { title: 'N/A', distance: 0 }
  }
  // Datos mock
  analysisData.value.avgDistance = 3.4

  try {
    const res = await analysis.getCantidadTareasPorSector()

    if (Array.isArray(res.data)) {
      analysisData.value.clusters = res.data.map((s, index) => ({
        sectorName: s.sector || `Sector ${index + 1}`,
        pendingCount: s.cantidadTareas || 0,
        tasks: [
          {
            id: index + 1,
            title: `Tareas pendientes en ${s.sector || 'este sector'}`
          }
        ]
      }))
    } else {
      analysisData.value.clusters = []
    }

    loadRadiusStats()
  } catch (error) {
    console.error("Error cargando análisis desde el servidor:", error)
    analysisData.value.clusters = []
  }
}

const loadRadiusStats = async () => {
  console.log("Cargando datos para radio de:", selectedRadius.value, "km")
  // MOCK DATA para el radio
  if(selectedRadius.value === '2') {
    analysisData.value.sectorsByRadius = [
      { id: 1, name: 'Construcción', completed: 15, percentage: 80 },
      { id: 2, name: 'Reparación', completed: 4, percentage: 20 }
    ]
  } else {
    analysisData.value.sectorsByRadius = [
      { id: 1, name: 'Construcción', completed: 45, percentage: 90 },
      { id: 2, name: 'Reparación', completed: 12, percentage: 40 },
      { id: 3, name: 'Aseo y Ornato', completed: 8, percentage: 15 }
    ]
  }
}

onMounted(() => {
  loadAnalysis()
})
</script>

<script>
export default { name: 'AnalysisNearestView' }
</script>

<style scoped>
.border-thin {
  border-bottom: 1px solid #e0e0e0 !important;
}
</style>