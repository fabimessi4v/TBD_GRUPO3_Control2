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
  

/**
 * Obtiene los sectores ordenados por cantidad de tareas completadas dentro de un radio de 2km
 * desde la ubicación del usuario autenticado.
 * Endpoint: GET /api/sectores/tareas-completadas/radio-2km
 */


getSectoresTareasCompletadasRadio2km() {
    return apiClient.get('/sectores/tareas-completadas/radio-2km')
  },

/**
 * Obtiene el promedio de distancia (en metros) entre la ubicación del usuario autenticado
 * y la ubicación de sus tareas completadas.
 * Endpoint: GET /api/promedios/distancia-tareas-completadas
 */


  getPromedioDistanciaTareasCompletadas() {
    return apiClient.get('/promedios/distancia-tareas-completadas')
  },

/**
 * Actualiza la posición en tiempo real del usuario autenticado.
 *
  * Endpoint: PUT /api/usuario/posicion-tiempo-real
 */
 UpdatePosicionUsuarioTiempoReal(posicion) {
  return apiClient.put('/usuario/posicion-tiempo-real', posicion)
},
/**
 * lista las tareas pendientes agrupadas por sector
 * Endpoint: GET /api/tarea/pendientes-por-sector
 */
 getTareasPendientesSector(){
  return apiClient.get('/tarea/pendientes-por-sector')
 },
/**
 * lista las tareas pendientes agrupadas por sector
 * Endpoint: GET /api/tarea/pendientes-por-sector
 */
 getTareasPendientesSectorDetallado(idSector){
  return apiClient.get(`/tarea/pendientes-por-sector/${idSector}`);
 },

}