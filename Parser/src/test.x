program {boolean j float i
   void factorial(int n, void answer)
     {if (n > 1) then
         { float ans
           ans = n*answer
           factorial(n-1,n*answer)
           return;
         }
         else
         { i = write(answer)
           return;
         }
     }
i = 1
while ( (i > 0) & (i < 5) ){
    i = i + 1
    factorial(i)
    }
}