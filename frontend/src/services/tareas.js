import apiClient from '@/services/axios'

export default {
  // Obtener notificaciones
  getNotificaciones() {
    return apiClient.get('/notificaciones/fechavencimiento')
  },
  // Obtener todas las tareas
  getTasks() {
    return apiClient.get('/tareas')
  },

  // Crear una nueva tarea con coordenadas PostGIS
  createTask(taskData) {
    return apiClient.post('/tareas', taskData)
  },

  // Eliminar tarea
  deleteTask(id) {
    return apiClient.delete(`/tareas/${id}`)
  },

  // Actualizar estado de completada
  updateStatus(id, isCompleted) {
    return apiClient.patch(`/tareas/${id}`, { completada: isCompleted })
  },

  // Obtener tareas por segun sesion actual de usuario
  getTasksUser() {
    return apiClient.get(`/tareas/mis-tareas`)
  },

  //actualizar tarea de usuario
  updateTask(id, taskData) {
  return apiClient.put(`/tareas/${id}`, taskData);
},
  //Obtener tareas finalizadas para cada usuario por sector
  getCompletedTasksBySector() {
    return apiClient.get('tareas/reportes/tareas-por-usuario-sector');
  },

  //Obtener tareas sector con mas tareas finalizadas por todos los usuarios
  getTopSectorByCompletedTasks() {
    return apiClient.get('tareas/reportes/sector-top-5km');
  }

}