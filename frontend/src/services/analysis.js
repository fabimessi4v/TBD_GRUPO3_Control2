// src/services/analysis.js
import apiClient from '@/services/axios'

export default {
  /**
   * Obtiene la cantidad de tareas por sector del usuario autenticado
   * Endpoint: GET /api/numerotareas/cantidadtareas
   */
  getCantidadTareasPorSector() {
    return apiClient.get('/numerotareas/cantidadtareas')
  },
  /**
   * Obtiene la cantidad de tareas por sector del usuario autenticado
   * Endpoint: GET /api/tarea/mascercana
   */
  getTareaMasCercana() {
    return apiClient.get('/tarea/mascercana')
  },
  // getPromedioDistancia() { ... }
}