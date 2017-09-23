package com.whiterabbit.pisabike

class FakeStationsProvider {
    val duomo : Station = Station("Duomo", "Pisa", 43.722860, 10.392210, "Piazza dei miracoli", 5, 2, 3, false, false)
    val aereoporto : Station = Station("Centro", "Pisa", 43.709962, 10.399015, "Piazza Vittorio Emanuele", 5, 2, 3, false, false)

    val list1 = listOf(duomo, aereoporto)
    val list2 = listOf(aereoporto, duomo)
}
