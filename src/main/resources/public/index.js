new Vue({
    el: '#stores-app',
    data: {
        stores: [],
    },
    mounted() {
        axios
            .get('/stores')
            .then(response => (this.stores = response.data))
    }
})