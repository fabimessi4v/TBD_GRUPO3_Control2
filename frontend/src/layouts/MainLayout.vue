<template>
  <v-app>
  <v-card>
      <v-layout>
        <v-navigation-drawer
          app
          expand-on-hover
          permanent
          rail
        >
        <v-list>
          <v-list-item>
          </v-list-item>
        </v-list>

        <v-divider></v-divider>

        <v-list density="compact" nav>
  <v-list-item
    prepend-icon="mdi-folder"
    title="Gestión de tareas"
    :to="'/tasks'"
    link
  />

  <v-list-item
    prepend-icon="mdi-map"
    title="Análisis geoespacial"
    :to="'/analysis/nearest'"
    link
  />

  <v-list-group value="reportes">
    <template #activator="{ props }">
      <v-list-item
        v-bind="props"
        prepend-icon="mdi-chart-bar"
        title="Reportes"
      />
    </template>

    <v-list-item
      prepend-icon="mdi-account-group"
      title="Tareas por usuario y sector"
      :to="'/reportes/user-sector'"
      link
    />

    <v-list-item
      prepend-icon="mdi-map-marker-radius"
      title="Sector top (5 km)"
      :to="'/reportes/top-sector'"
      link
    />
  </v-list-group>
</v-list>

       

        <v-divider class="my-2"></v-divider>

        <v-list density="compact" nav>
          <v-list-item
            prepend-icon="mdi-logout"
            title="Cerrar sesión"
            value="logout"
            @click="onLogout"
          ></v-list-item>
        </v-list>
      </v-navigation-drawer>

        <v-main class="main-content">

          <router-view />
        </v-main>
      </v-layout>
    </v-card>
  </v-app>
</template>

<script>
import AuthService from '@/services/login'

export default {
  name: 'MainLayout',
  methods: {
    onLogout() {
      AuthService.logout()
      this.$router.push('/login')
    },
  },
}
</script>