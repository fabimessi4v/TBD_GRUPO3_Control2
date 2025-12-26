<template>
  <v-container>
    <div class="d-flex align-center mb-4">
      <h1 class="text-h5">Reporte: Tareas realizadas por usuario y sector</h1>
      <v-spacer />
      <v-btn color="primary" variant="tonal" @click="cargar" :loading="loading">
        Recargar
      </v-btn>
    </div>

    <v-alert v-if="errorMessage" type="error" variant="tonal" class="mb-4">
      {{ errorMessage }}
    </v-alert>

    <v-data-table
      :headers="headers"
      :items="rows"
      :loading="loading"
      class="elevation-1"
      fixed-header
      height="70vh"
    />
  </v-container>
</template>

<script>
import reportes from '@/services/tareas'

export default {
  name: 'UserSector',
  data() {
    return {
      loading: false,
      errorMessage: '',
      rows: [],
      headers: [
        { title: 'Usuario', key: 'username' },
        { title: 'Sector', key: 'sector' },
        { title: 'Tareas realizadas', key: 'tareasRealizadas', align: 'end' },
      ],
    }
  },
  mounted() {
    this.cargar()
  },
  methods: {
    async cargar() {
      this.loading = true
      this.errorMessage = ''
      try {
        const res = await reportes.getCompletedTasksBySector()
        this.rows = Array.isArray(res.data) ? res.data : (res.data?.data ?? [])
        this.rows = res.data || []
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
