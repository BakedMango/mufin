'use client';

interface DealType {
  name: string | number;
  count: string | number;
  totalPrice: string | number;
}

interface DealListProps {
  deals: DealType[];
}

export default function ListDeal({ deals }: DealListProps) {
  return (
    <div className="grid grid-cols-7 w-full bg-custom-white p-[0.5rem] rounded-[0.8rem] mt-[0.5rem]">
      <div className="col-span-4 text-[1rem] text-custom-medium-gray flex items-center  border-b mb-[0.3rem]">
        거래내역명
      </div>
      <div className="text-[1rem]  text-custom-medium-gray flex items-center justify-center border-b mb-[0.3rem]">
        수량
      </div>
      <div className="col-span-2 text-[1rem] text-custom-medium-gray flex items-center justify-end border-b  pr-[0.5rem]  mb-[0.3rem]">
        지출금액
      </div>
      {deals.map(({ name, count, totalPrice }, index) => {
        return (
          <>
            <div className="col-span-4 text-[1.2rem] font-normal pl-[0.3rem] text-custom-dark-gray">
              <p>{name}</p>
            </div>
            <div className="text-[1.2rem] font-normal text-center text-custom-dark-gray">
              <p>{count}</p>
            </div>
            <div className="col-span-2 text-[1.2rem] font-normal text-right pr-[0.5rem] text-custom-dark-gray">
              <p>{totalPrice}원</p>
            </div>
          </>
        );
      })}
    </div>
  );
}
