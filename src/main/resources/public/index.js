const StoreList = {
    data() {
        return {
            stores: [],
            currentStore: null,
            currentPage: 1,
            nameSearch: '',
        }
    },
    watch: {
        nameSearch() {
            this.getStores()
            this.currentPage = 1
        },
        currentPage() {
            this.currentStore = null
            this.getStores()
        },
    },
    methods: {
        nextPage() {
            this.currentPage++
        },
        previousPage() {
            this.currentPage--
        },
        getStores() {
            axios
                .get('/stores', {
                    params: {
                        page: this.currentPage,
                        nameSearch: this.nameSearch,
                    }
                })
                .then(response => this.stores = response.data)
        }
    },
    mounted() {
        this.getStores()
    }
}

Vue.createApp(StoreList).mount('#stores-app')