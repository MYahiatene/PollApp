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
                    background: '#000000',
                    primary: '#26A69A',
                    accent: '#992078',
                    secondary: '#337982',
                    info: '#114955',
                    warning: '#F5D63C',
                    error: '#F5420C',
                    success: '#5E9112',
                    myColor: '#1f5355',
                    lightColor: '#eeffff',
                    background2: '#101214',
                    softAccent: '#072d38',
                    header: '#151b24',
                },
                light: {
                    background: '#eefcfa',
                    primary: '#114955',
                    accent: '#551044',
                    secondary: '#337982',
                    info: '#26A69A',
                    warning: '#F5D63C',
                    error: '#F5420C',
                    success: '#8EC136',
                    myColor: '#1f5355',
                    lightColor: '#eeffff',
                    // these colors work for darkMode as well as for lightMode
                    neutralHighlightColor: '#88cccc',
                    neutralGray: '#888888',
                    //
                    background2: '#a0b4b8',
                    softAccent: '#8dc4c3',
                    header: '#698f96',
                },
            },
        },
    },
    // added to allow devtools
    vue: {
        config: {
            productionTip: false,
            devtools: true,
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
