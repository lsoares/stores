const StoreList = {
    data() {
        return {
            stores: [],
            currentPage: 1,
        }
    },
    methods: {
        nextPage() {
            this.currentPage++
            this.getStores()
        },
        previousPage() {
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