// Listar Vendas por Produto e Cidade

db.sales_order_lines.aggregate([
    { $limit: 10},
    {
        $lookup: {
          from: "sales_orders",
          localField: "salesOrderID",
          foreignField: "salesOrderID",
          as: "orderDetails"
        }
      },
      { $unwind: { path: "$orderDetails", preserveNullAndEmptyArrays: true } },
      {
        $lookup: {
          from: "sales_territories",
          localField: "orderDetails.salesTerritoryID",
          foreignField: "salesTerritoryID",
          as: "territoryDetails"
        }
      },
      { $unwind: { path: "$territoryDetails", preserveNullAndEmptyArrays: true } },
      {
        $lookup: {
          from: "states",
          localField: "territoryDetails.countryID",
          foreignField: "countryID",
          as: "stateDetails"
        }
      },
      { $unwind: { path: "$stateDetails", preserveNullAndEmptyArrays: true } },
      {
        $lookup: {
          from: "cities",
          localField: "stateDetails.stateID",
          foreignField: "stateID",
          as: "cityDetails"
        }
      },
      { $unwind: { path: "$cityDetails", preserveNullAndEmptyArrays: true } },
      {
        $group: {
          _id: { productID: "$productID", cityName: "$cityDetails.cityName" },
          totalQuantity: { $sum: "$salesOrderLineQuantity" },
          totalSales: {
            $sum: {
                $multiply: [
                  { $toDouble: "$salesOrderLineQuantity" },
                  { $toDouble: "$salesOrderLineUnitPrice" }
                ]
            }
        }
        }
      },
      {
        $project: {
          _id: 0,
          productID: "$_id.productID",
          cityName: "$_id.cityName",
          totalQuantity: 1,
          totalSales: 1
        }
      }
    ]);
  

// Vendas Totais e Média por Mês/Ano 

db.sales_orders.aggregate([
    { $limit: 10},
    {
      $lookup: {
        from: "sales_order_lines",
        localField: "salesOrderID",
        foreignField: "salesOrderID",
        as: "orderLines"
      }
    },
    { $unwind: "$orderLines" },
    {
      $addFields: {
        salesOrderDateParsed: { $toDate: "$salesOrderDate" },
        "orderLines.salesOrderLineQuantity": { $toDouble: "$orderLines.salesOrderLineQuantity" },
        "orderLines.salesOrderLineUnitPrice": { $toDouble: "$orderLines.salesOrderLineUnitPrice" }
      }
    },
    {
      $group: {
        _id: {
          year: { $year: "$salesOrderDateParsed" },
          month: { $month: "$salesOrderDateParsed" },
          productID: "$orderLines.productID"
        },
        totalSales: {
          $sum: { $multiply: ["$orderLines.salesOrderLineQuantity", "$orderLines.salesOrderLineUnitPrice"] }
        },
        averageMonthlySales: {
          $avg: { $multiply: ["$orderLines.salesOrderLineQuantity", "$orderLines.salesOrderLineUnitPrice"] }
        }
      }
    }
  ]);
  

// Listar Produtos e Quantidades por Modelo

db.products.aggregate([
    { $limit: 10},
  {
    $lookup: {
      from: "sales_order_lines",
      localField: "productID",
      foreignField: "productID",
      as: "salesData"
    }
  },
  { $unwind: "$salesData" },
  {
    $group: {
      _id: { modelID: "$modelID", productName: "$productName" },
      totalQuantity: { $sum: "$salesData.salesOrderLineQuantity" }
    }
  },
]);