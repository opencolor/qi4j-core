/*
 * Copyright (c) 2007, Rickard Öberg. All Rights Reserved.
 * Copyright (c) 2007, Niclas Hedhman. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package iop.runtime.modifier;

import iop.api.ObjectRepository;
import iop.api.annotation.Uses;
import iop.api.annotation.Modifies;
import iop.api.persistence.binding.PersistenceBinding;
import iop.api.persistence.ObjectNotFoundException;
import iop.api.persistence.PersistentRepository;
import iop.runtime.ObjectRepositoryCache;

/**
 * Implement caching of created proxies to persistent objects.
 *
 */
public final class ObjectRepositoryCacheModifier
   implements ObjectRepository
{
   @Uses ObjectRepositoryCache cache;
   @Modifies ObjectRepository repository;

   public <T extends PersistenceBinding> T getInstance(String anIdentity, Class<T> aType)
   {
      // Check cache
      PersistenceBinding cachedObj = cache.getObject(anIdentity);
      if (cachedObj != null)
      {
         return (T) cachedObj;
      }

      // Not found in cache - create it
      cachedObj = repository.getInstance(anIdentity, aType);

      // Add to cache
      cache.addObject(anIdentity, cachedObj);

      return (T) cachedObj;
   }
}