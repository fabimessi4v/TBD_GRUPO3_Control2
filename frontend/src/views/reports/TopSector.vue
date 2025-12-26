<template>
  <v-container>
    <div class="d-flex align-center mb-4">
      <h1 class="text-h5">Reporte: Sector con más tareas completadas a 5 km</h1>
      <v-spacer />
      <v-btn color="primary" @click="cargar" :loading="loading">
        Consultar
      </v-btn>
    </div>

    <v-alert v-if="errorMessage" type="error" variant="tonal" class="mb-4">
      {{ errorMessage }}
    </v-alert>

    <v-card v-if="resultado" class="pa-4" variant="outlined">
      <div class="text-subtitle-1 mb-2">Resultado</div>
      <div><strong>Sector:</strong> {{ resultado.sector }}</div>
      <div><strong>Tareas completadas (5 km):</strong> {{ resultado.tareasCompletadas5km }}</div>
      <div v-if="resultado.sectorId != null"><strong>ID sector:</strong> {{ resultado.sectorId }}</div>
    </v-card>

    <v-alert v-else-if="consultado && !loading" type="info" variant="tonal">
      No hay datos (o no hay tareas completadas dentro de 5 km).
    </v-alert>
  </v-container>
</template>

<script>
import tareas from '@/services/tareas'

export default {
  name: 'TopSector',
  data() {
    return {
      loading: false,
      consultado: false,
      errorMessage: '',
      resultado: null,
    }
  },
  mounted() {
    this.cargar() // opcional: auto cargar
  },
  methods: {
    async cargar() {
      this.loading = true
      this.consultado = true
      this.errorMessage = ''
      this.resultado = null
      try {
        const res = await tareas.getTopSectorByCompletedTasks()
        this.resultado = res.data || null
      } catch (error) {
        console.error(error)
        this.errorMessage =
          error?.response?.data?.message ||
          error?.response?.data?.error ||
          'Error al cargar el reporte.'
      } finally {
        this.loading = false
      }
    },
  },
}
</script>
