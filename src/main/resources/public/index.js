const StoreList = {
    data() {
        return {
            stores: [],
        }
    },
    mounted() {
        axios
            .get('/stores')
            .then(response => this.stores = response.data)
    }
}

Vue.createApp(StoreList).mount('#stores-app')