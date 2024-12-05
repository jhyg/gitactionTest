<template>

    <v-data-table
        :headers="headers"
        :items="getStock"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'GetStockView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "id", value: "id" },
            ],
            getStock : [],
        }),
          async created() {
            var temp = await axios.get(axios.fixUrl('/getStocks'))

            temp.data._embedded.getStocks.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

            this.getStock = temp.data._embedded.getStocks;
        },
        methods: {
        }
    }
</script>

