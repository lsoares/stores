const StoreList = {
    data() {
        return {
            stores: [],
            currentStore: null,
            currentPage: 1,
        }
    },
    methods: {
        nextPage() {
            this.currentStore = null
            this.currentPage++
            this.getStores()
        },
        previousPage() {
            this.currentStore = null
            this.currentPage--
            this.getStores()
        },
        getStores() {
            axios
                .get('/stores', {params: {page: this.currentPage}})
                .then(response => this.stores = response.data)
        }
    },
    mounted() {
        this.getStores()
    }
}

Vue.createApp(StoreList).mount('#stores-app')