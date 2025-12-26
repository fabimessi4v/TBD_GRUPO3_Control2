import { createRouter, createWebHistory } from 'vue-router'
import Tasks from '../views/Tasks.vue'
import CreateTask from '../views/CreateTask.vue'
import Nearest from '../views/analysis/Nearest.vue'
import Radius from '../views/analysis/Radius.vue'
import Clusters from '../views/analysis/Clusters.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import TopSector from '../views/reports/TopSector.vue'
import UserSector from '../views/reports/UserSector.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/tasks', name: 'Tasks', component: Tasks },
  { path: '/tasks/create', name: 'CreateTask', component: CreateTask },
  { path: '/analysis/nearest', name: 'Nearest', component: Nearest },
  { path: '/analysis/radius', name: 'Radius', component: Radius },
  { path: '/analysis/clusters', name: 'Clusters', component: Clusters },
  { path: '/reportes/user-sector', name: 'UserSector', component: UserSector },
  { path: '/reportes/top-sector', name: 'SectorTop5km', component: TopSector },
  { path: '/:pathMatch(.*)*', redirect: '/login' },
  


]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const isAuthRoute = to.path === '/login' || to.path === '/register'
  const hasToken = !!localStorage.getItem('token')

  if (!hasToken && !isAuthRoute) return '/login'
  if (hasToken && isAuthRoute) return '/tasks'

  return true
})

export default router
