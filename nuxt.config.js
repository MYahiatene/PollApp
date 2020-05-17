import colors from 'vuetify/es5/util/colors'

export default {
    mode: 'universal',

    srcDir: 'src/main/vue',
    server: {
        port: 8080,
    },

    head: {
        titleTemplate: '%s - ' + process.env.npm_package_name,
        title: process.env.npm_package_name || '',
        meta: [
            { charset: 'utf-8' },
            { name: 'viewport', content: 'width=device-width, initial-scale=1' },
            {
                hid: 'description',
                name: 'description',
                content: process.env.npm_package_description || '',
            },
        ],
        link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }],
    },
    /*
     ** Customize the progress-bar color
     */
    loading: { color: '#fff' },
    /*
     ** Global CSS
     */
    css: [],
    /*
     ** Plugins to load before mounting the App
     */
    plugins: [],
    /*
     ** Nuxt.js dev-modules
     */
    buildModules: [
        // Doc: https://github.com/nuxt-community/eslint-module
        '@nuxtjs/eslint-module',
        '@nuxtjs/vuetify',
    ],
    /*
     ** Nuxt.js modules
     */
    modules: ['@nuxtjs/axios', '@nuxtjs/proxy'],
    /*
     ** vuetify module configuration
     ** https://github.com/nuxt-community/vuetify-module
     */
    vuetify: {
        customVariables: ['./src/main/vue/assets/variables.scss'],
        theme: {
            dark: false,
            themes: {
                dark: {
                    background: '#09181a',
                    primary: colors.teal.lighten1,
                    accent: '#992078',
                    secondary: '#337982',
                    info: '#114955',
                    warning: '#F5D63C',
                    error: '#F5420C',
                    success: '#5E9112',
                    myColor: '#1f5355',
                },
                light: {
                    background: '#eefcfa',
                    primary: '#114955',
                    accent: '#551044',
                    secondary: '#337982',
                    info: colors.teal.lighten1,
                    warning: '#F5D63C',
                    error: '#F5420C',
                    success: '#8EC136',
                    myColor: '#1f5355',
                },
            },
        },
    },
    /*
     ** Build configuration
     */
    build: {
        /*
         ** You can extend webpack config here
         */
        extend(config, ctx) {},
    },
}
