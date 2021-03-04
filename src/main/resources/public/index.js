const StoreList = {
    data() {
        return {
            stores: [],
            currentStore: null,
            currentPage: 1,
            nameSearch: null,
            newStoreName: null,
        }
    },
    watch: {
        nameSearch() {
            this.getStores()
            this.currentPage = 1
            this.currentStore = null
        },
        currentPage() {
            this.currentStore = null
            this.getStores()
        },
        currentStore(val) {
            if (val && val.name === null) val.name = '';
            this.newStoreName = null
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
        },
        confirmStoreName(storeId) {
            axios
                .patch(`/stores/${storeId}`, {newName: this.newStoreName})
                .then(() => {
                    this.currentStore.name = this.newStoreName
                    this.newStoreName = null
                })
                .then(this.getStores)
            return false
        },
    },
    mounted() {
        this.getStores()
    },
}

Vue.createApp(StoreList).mount('#stores-app')