<template>
    <v-card>
        <v-card-title>QR-Code zu {{ qrTitle }} konfigurieren</v-card-title>
        <v-row>
            <v-col>
                <v-row>
                    <v-col>
                        <v-label> Vordergrundfarbe:</v-label>
                        <v-color-picker v-model="qrForeground" />
                    </v-col>
                    <v-col>
                        <v-label>Hintergrundfarbe:</v-label>
                        <v-color-picker v-model="qrBackground" />
                    </v-col>
                </v-row>
                <v-row>
                    <v-col>
                        <v-label
                            >Redundanzbits in Prozent: (Mehr Redundanz: Robuster gegen Verdeckung aber größer und
                            schwieriger zu Scannen)</v-label
                        >
                        <v-overflow-btn v-model="qrSelectedLevel" label="Redundanz" :items="qrLevelTexts" />
                    </v-col>
                </v-row>
                <v-row>
                    <v-col>
                        <v-label>Größe in Pixel:</v-label>
                        <v-text-field v-model="qrSize" label="Höhe/Breite des Qr Codes" type="Number" />
                    </v-col>
                </v-row>
            </v-col>
            <v-col>
                <v-card-subtitle>
                    Zum Kopieren oder Speichern einfach mit der rechten Maustaste in den QR-Code clicken
                </v-card-subtitle>
                <v-card class="ma-8">
                    <qrcode-vue
                        :value="qrLink"
                        :size="qrSize"
                        :level="qrLevels[qrLevelTexts.indexOf(qrSelectedLevel)]"
                        :foreground="qrForeground"
                        :background="qrBackground"
                    />
                </v-card>
            </v-col>
        </v-row>
    </v-card>
</template>

<script>
import QrcodeVue from 'qrcode.vue'
import { mapGetters } from 'vuex'

export default {
    name: 'qrGenerator',
    components: {
        QrcodeVue,
    },
    data() {
        return {
            qrSize: 300,
            qrLevels: ['L', 'M', 'Q', 'H'],
            qrLevelTexts: ['7%', '15%', '25%', '30%'],
            qrSelectedLevel: '25%',
            qrForeground: '#000000FF',
            qrBackground: '#FFFFFFFF',
        }
    },
    computed: {
        ...mapGetters({
            qrTitle: 'navigation/getQrTitle',
            qrLink: 'navigation/getQrLink',
        }),
    },
}
</script>

<style scoped></style>
